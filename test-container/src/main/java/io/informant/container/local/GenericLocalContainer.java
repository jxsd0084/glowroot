/*
 * Copyright 2011-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.informant.container.local;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import checkers.nullness.quals.Nullable;
import com.google.common.base.Charsets;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;

import io.informant.InformantModule;
import io.informant.MainEntryPoint;
import io.informant.config.PluginDescriptorCache;
import io.informant.container.Container.StartupFailedException;
import io.informant.container.SpyingLogFilter;
import io.informant.container.SpyingLogFilter.MessageCount;
import io.informant.container.SpyingLogFilterCheck;
import io.informant.container.TempDirs;
import io.informant.container.Threads;
import io.informant.container.config.ConfigService;
import io.informant.container.trace.TraceService;
import io.informant.markers.ThreadSafe;
import io.informant.trace.AdhocAdviceCache;
import io.informant.weaving.IsolatedWeavingClassLoader;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * @author Trask Stalnaker
 * @since 0.5
 */
@ThreadSafe
public class GenericLocalContainer<T> {

    private final File dataDir;
    private final boolean deleteDataDirOnClose;
    private final boolean shared;

    private final Collection<Thread> preExistingThreads;
    private final IsolatedWeavingClassLoader isolatedWeavingClassLoader;
    private final Class<T> appInterface;
    private final AppExecutor<T> appExecutor;
    private final LocalConfigService configService;
    private final LocalTraceService traceService;
    private final List<Thread> executingAppThreads = Lists.newCopyOnWriteArrayList();
    private final InformantModule informantModule;

    public GenericLocalContainer(@Nullable File dataDir, boolean useFileDb, boolean shared,
            Class<T> appInterface, AppExecutor<T> appExecutor) throws Exception {
        if (dataDir == null) {
            this.dataDir = TempDirs.createTempDir("informant-test-datadir");
            deleteDataDirOnClose = true;
        } else {
            this.dataDir = dataDir;
            deleteDataDirOnClose = false;
        }
        this.shared = shared;
        preExistingThreads = Threads.currentThreads();
        // default to port 0 (any available)
        File configFile = new File(this.dataDir, "config.json");
        if (!configFile.exists()) {
            Files.write("{\"ui\":{\"port\":0}}", configFile, Charsets.UTF_8);
        }
        Map<String, String> properties = Maps.newHashMap();
        properties.put("data.dir", this.dataDir.getAbsolutePath());
        if (!useFileDb) {
            properties.put("internal.h2.memdb", "true");
        }
        try {
            MainEntryPoint.start(properties);
            informantModule = MainEntryPoint.getInformantModule();
        } catch (Throwable t) {
            throw new StartupFailedException(t);
        }
        IsolatedWeavingClassLoader.Builder loader = IsolatedWeavingClassLoader.builder();
        PluginDescriptorCache pluginDescriptorCache =
                informantModule.getConfigModule().getPluginDescriptorCache();
        AdhocAdviceCache adhocAdviceCache =
                informantModule.getTraceModule().getDynamicAdviceCache();
        loader.setMixinTypes(pluginDescriptorCache.getMixinTypes());
        loader.setAdvisors(Iterables.concat(pluginDescriptorCache.getAdvisors(),
                adhocAdviceCache.getAdhocAdvisors()));
        loader.setMetricTimerService(informantModule.getTraceModule().getMetricTimerService());
        loader.addBridgeClasses(appInterface);
        loader.addExcludePackages("io.informant.api", "io.informant.collector",
                "io.informant.common", "io.informant.config", "io.informant.dynamicadvice",
                "io.informant.local", "io.informant.trace", "io.informant.weaving",
                "io.informant.shaded", "io.informant.testkit");
        loader.setWeavingDisabled(informantModule.getConfigModule().getConfigService()
                .getAdvancedConfig().isWeavingDisabled());
        isolatedWeavingClassLoader = loader.build();
        this.appInterface = appInterface;
        this.appExecutor = appExecutor;
        configService = new LocalConfigService(informantModule);
        traceService = new LocalTraceService(informantModule);
    }

    public ConfigService getConfigService() {
        return configService;
    }

    public void addExpectedLogMessage(String loggerName, String partialMessage) {
        if (SpyingLogFilterCheck.isSpyingLogFilterEnabled()) {
            SpyingLogFilter.addExpectedMessage(loggerName, partialMessage);
        } else {
            throw new AssertionError(SpyingLogFilter.class.getSimpleName()
                    + " is not enabled");
        }
    }

    public void executeAppUnderTest(Class<? extends T> appClass) throws Exception {
        ClassLoader previousContextClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(isolatedWeavingClassLoader);
        executingAppThreads.add(Thread.currentThread());
        try {
            T app = isolatedWeavingClassLoader.newInstance(appClass, appInterface);
            appExecutor.executeApp(app);
        } finally {
            executingAppThreads.remove(Thread.currentThread());
            Thread.currentThread().setContextClassLoader(previousContextClassLoader);
        }
        // wait for all traces to be stored
        Stopwatch stopwatch = new Stopwatch().start();
        while (traceService.getNumPendingCompleteTraces() > 0 && stopwatch.elapsed(SECONDS) < 5) {
            Thread.sleep(10);
        }
    }

    public void interruptAppUnderTest() throws Exception {
        for (Thread thread : executingAppThreads) {
            thread.interrupt();
        }
    }

    public TraceService getTraceService() {
        return traceService;
    }

    public int getUiPort() {
        return informantModule.getUiModule().getPort();
    }

    public void checkAndReset() throws Exception {
        traceService.assertNoActiveTraces();
        traceService.deleteAllSnapshots();
        configService.resetAllConfig();
        // check and reset log messages
        if (SpyingLogFilterCheck.isSpyingLogFilterEnabled()) {
            MessageCount logMessageCount = SpyingLogFilter.clearMessages();

            if (logMessageCount.getExpectedCount() > 0) {
                throw new AssertionError("One or more expected messages were not logged");
            }
            if (logMessageCount.getUnexpectedCount() > 0) {
                throw new AssertionError("One or more unexpected messages were logged");
            }
        }
    }

    public void close() throws Exception {
        close(false);
    }

    public void close(boolean evenIfShared) throws Exception {
        if (shared && !evenIfShared) {
            // this is the shared container and will be closed at the end of the run
            return;
        }
        Threads.preShutdownCheck(preExistingThreads);
        informantModule.close();
        Threads.postShutdownCheck(preExistingThreads);
        if (deleteDataDirOnClose) {
            TempDirs.deleteRecursively(dataDir);
        }
    }

    public interface AppExecutor<T> {
        void executeApp(T appUnderTest) throws Exception;
    }
}
