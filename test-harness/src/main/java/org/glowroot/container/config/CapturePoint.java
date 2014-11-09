/*
 * Copyright 2013-2014 the original author or authors.
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
package org.glowroot.container.config;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import static org.glowroot.container.common.ObjectMappers.checkNotNullItemsForProperty;
import static org.glowroot.container.common.ObjectMappers.checkNotNullValuesForProperty;
import static org.glowroot.container.common.ObjectMappers.checkRequiredProperty;

public class CapturePoint {

    @Nullable
    private String className;
    @Nullable
    private String methodName;
    private ImmutableList<String> methodParameterTypes;
    @Nullable
    private String methodReturnType;
    private ImmutableList<MethodModifier> methodModifiers;
    @Nullable
    private CaptureKind captureKind;

    @Nullable
    private String metricName;

    @Nullable
    private String traceEntryTemplate;
    @Nullable
    private Long traceEntryStackThresholdMillis;
    private boolean traceEntryCaptureSelfNested;

    @Nullable
    private String transactionType;
    @Nullable
    private String transactionNameTemplate;
    @Nullable
    private String transactionUserTemplate;
    @Nullable
    private Map<String, String> transactionCustomAttributeTemplates;

    @Nullable
    private Long traceStoreThresholdMillis;

    @Nullable
    private String enabledProperty;
    @Nullable
    private String traceEntryEnabledProperty;

    // null for new CapturePoint records that haven't been sent to server yet
    @Nullable
    private final String version;

    // used to create new CapturePoint records that haven't been sent to server yet
    public CapturePoint() {
        methodParameterTypes = ImmutableList.of();
        methodModifiers = ImmutableList.of();
        transactionCustomAttributeTemplates = ImmutableMap.of();
        version = null;
    }

    public CapturePoint(String version) {
        methodParameterTypes = ImmutableList.of();
        methodModifiers = ImmutableList.of();
        transactionCustomAttributeTemplates = ImmutableMap.of();
        this.version = version;
    }

    @Nullable
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    @Nullable
    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public ImmutableList<String> getMethodParameterTypes() {
        return methodParameterTypes;
    }

    public void setMethodParameterTypes(List<String> methodParameterTypes) {
        this.methodParameterTypes = ImmutableList.copyOf(methodParameterTypes);
    }

    @Nullable
    public String getMethodReturnType() {
        return methodReturnType;
    }

    public void setMethodReturnType(String methodReturnType) {
        this.methodReturnType = methodReturnType;
    }

    public ImmutableList<MethodModifier> getMethodModifiers() {
        return methodModifiers;
    }

    public void setMethodModifiers(List<MethodModifier> methodModifiers) {
        this.methodModifiers = ImmutableList.copyOf(methodModifiers);
    }

    @Nullable
    public CaptureKind getCaptureKind() {
        return captureKind;
    }

    public void setCaptureKind(CaptureKind captureKind) {
        this.captureKind = captureKind;
    }

    @Nullable
    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    @Nullable
    public String getTraceEntryTemplate() {
        return traceEntryTemplate;
    }

    public void setTraceEntryTemplate(String traceEntryTemplate) {
        this.traceEntryTemplate = traceEntryTemplate;
    }

    @Nullable
    public Long getTraceEntryStackThresholdMillis() {
        return traceEntryStackThresholdMillis;
    }

    public void setTraceEntryStackThresholdMillis(@Nullable Long traceEntryStackThresholdMillis) {
        this.traceEntryStackThresholdMillis = traceEntryStackThresholdMillis;
    }

    public boolean isTraceEntryCaptureSelfNested() {
        return traceEntryCaptureSelfNested;
    }

    public void setTraceEntryCaptureSelfNested(boolean traceEntryCaptureSelfNested) {
        this.traceEntryCaptureSelfNested = traceEntryCaptureSelfNested;
    }

    @Nullable
    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    @Nullable
    public String getTransactionNameTemplate() {
        return transactionNameTemplate;
    }

    public void setTransactionNameTemplate(String transactionNameTemplate) {
        this.transactionNameTemplate = transactionNameTemplate;
    }

    @Nullable
    public String getTransactionUserTemplate() {
        return transactionUserTemplate;
    }

    public void setTransactionUserTemplate(String transactionUserTemplate) {
        this.transactionUserTemplate = transactionUserTemplate;
    }

    @Nullable
    public Map<String, String> getTransactionCustomAttributeTemplates() {
        return transactionCustomAttributeTemplates;
    }

    public void setTransactionCustomAttributeTemplates(
            Map<String, String> transactionCustomAttributeTemplates) {
        this.transactionCustomAttributeTemplates = transactionCustomAttributeTemplates;
    }

    @Nullable
    public Long getTraceStoreThresholdMillis() {
        return traceStoreThresholdMillis;
    }

    public void setTraceStoreThresholdMillis(@Nullable Long traceStoreThresholdMillis) {
        this.traceStoreThresholdMillis = traceStoreThresholdMillis;
    }

    @Nullable
    public String getEnabledProperty() {
        return enabledProperty;
    }

    public void setEnabledProperty(String enabledProperty) {
        this.enabledProperty = enabledProperty;
    }

    @Nullable
    public String getTraceEntryEnabledProperty() {
        return traceEntryEnabledProperty;
    }

    public void setTraceEntryEnabledProperty(String traceEntryEnabledProperty) {
        this.traceEntryEnabledProperty = traceEntryEnabledProperty;
    }

    @Nullable
    public String getVersion() {
        return version;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof CapturePoint) {
            CapturePoint that = (CapturePoint) obj;
            // intentionally leaving off version since it represents the prior version hash when
            // sending to the server, and represents the current version hash when receiving from
            // the server
            return Objects.equal(className, that.className)
                    && Objects.equal(methodName, that.methodName)
                    && Objects.equal(methodParameterTypes, that.methodParameterTypes)
                    && Objects.equal(methodReturnType, that.methodReturnType)
                    && Objects.equal(methodModifiers, that.methodModifiers)
                    && Objects.equal(captureKind, that.captureKind)
                    && Objects.equal(metricName, that.metricName)
                    && Objects.equal(traceEntryTemplate, that.traceEntryTemplate)
                    && Objects.equal(traceEntryStackThresholdMillis,
                            that.traceEntryStackThresholdMillis)
                    && Objects.equal(traceEntryCaptureSelfNested, that.traceEntryCaptureSelfNested)
                    && Objects.equal(transactionType, that.transactionType)
                    && Objects.equal(transactionNameTemplate, that.transactionNameTemplate)
                    && Objects.equal(transactionUserTemplate, that.transactionUserTemplate)
                    && Objects.equal(transactionCustomAttributeTemplates,
                            that.transactionCustomAttributeTemplates)
                    && Objects.equal(traceStoreThresholdMillis, that.traceStoreThresholdMillis)
                    && Objects.equal(enabledProperty, that.enabledProperty)
                    && Objects.equal(traceEntryEnabledProperty, that.traceEntryEnabledProperty);
        }
        return false;
    }

    @Override
    public int hashCode() {
        // intentionally leaving off version since it represents the prior version hash when
        // sending to the server, and represents the current version hash when receiving from the
        // server
        return Objects.hashCode(className, methodName, methodParameterTypes, methodReturnType,
                methodModifiers, captureKind, metricName, traceEntryTemplate,
                traceEntryStackThresholdMillis, traceEntryCaptureSelfNested, transactionType,
                transactionNameTemplate, transactionUserTemplate,
                transactionCustomAttributeTemplates, traceStoreThresholdMillis, enabledProperty,
                traceEntryEnabledProperty);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("className", className)
                .add("methodName", methodName)
                .add("methodParameterTypes", methodParameterTypes)
                .add("methodReturnType", methodReturnType)
                .add("methodModifiers", methodModifiers)
                .add("captureKind", captureKind)
                .add("metricName", metricName)
                .add("traceEntryTemplate", traceEntryTemplate)
                .add("traceEntryStackThresholdMillis", traceEntryStackThresholdMillis)
                .add("traceEntryCaptureSelfNested", traceEntryCaptureSelfNested)
                .add("transactionType", transactionType)
                .add("transactionNameTemplate", transactionNameTemplate)
                .add("transactionUserTemplate", transactionUserTemplate)
                .add("transactionCustomAttributeTemplates", transactionCustomAttributeTemplates)
                .add("traceStoreThresholdMillis", traceStoreThresholdMillis)
                .add("enabledProperty", enabledProperty)
                .add("traceEntryEnabledProperty", traceEntryEnabledProperty)
                .add("version", version)
                .toString();
    }

    @JsonCreator
    static CapturePoint readValue(
            @JsonProperty("className") @Nullable String className,
            @JsonProperty("methodName") @Nullable String methodName,
            @JsonProperty("methodParameterTypes") @Nullable List</*@Nullable*/String> uncheckedMethodParameterTypes,
            @JsonProperty("methodReturnType") @Nullable String methodReturnType,
            @JsonProperty("methodModifiers") @Nullable List</*@Nullable*/MethodModifier> uncheckedMethodModifiers,
            @JsonProperty("captureKind") @Nullable CaptureKind captureKind,
            @JsonProperty("metricName") @Nullable String metricName,
            @JsonProperty("traceEntryTemplate") @Nullable String traceEntryTemplate,
            @JsonProperty("traceEntryStackThresholdMillis") @Nullable Long traceEntryStackThresholdMillis,
            @JsonProperty("traceEntryCaptureSelfNested") @Nullable Boolean traceEntryCaptureSelfNested,
            @JsonProperty("transactionType") @Nullable String transactionType,
            @JsonProperty("transactionNameTemplate") @Nullable String transactionNameTemplate,
            @JsonProperty("transactionUserTemplate") @Nullable String transactionUserTemplate,
            @JsonProperty("transactionCustomAttributeTemplates") @Nullable Map<String, /*@Nullable*/String> uncheckedTransactionCustomAttributeTemplates,
            @JsonProperty("traceStoreThresholdMillis") @Nullable Long traceStoreThresholdMillis,
            @JsonProperty("enabledProperty") @Nullable String enabledProperty,
            @JsonProperty("traceEntryEnabledProperty") @Nullable String traceEntryEnabledProperty,
            @JsonProperty("version") @Nullable String version) throws JsonMappingException {
        List<String> methodParameterTypes =
                checkNotNullItemsForProperty(uncheckedMethodParameterTypes, "methodParameterTypes");
        List<MethodModifier> methodModifiers =
                checkNotNullItemsForProperty(uncheckedMethodModifiers, "methodModifiers");
        Map<String, String> transactionCustomAttributeTemplates =
                checkNotNullValuesForProperty(uncheckedTransactionCustomAttributeTemplates,
                        "transactionCustomAttributeTemplates");
        checkRequiredProperty(className, "className");
        checkRequiredProperty(methodName, "methodName");
        checkRequiredProperty(methodParameterTypes, "methodParameterTypes");
        checkRequiredProperty(methodReturnType, "methodReturnType");
        checkRequiredProperty(methodModifiers, "methodModifiers");
        checkRequiredProperty(captureKind, "captureKind");
        checkRequiredProperty(metricName, "metricName");
        checkRequiredProperty(traceEntryTemplate, "traceEntryTemplate");
        checkRequiredProperty(traceEntryCaptureSelfNested, "traceEntryCaptureSelfNested");
        checkRequiredProperty(transactionType, "transactionType");
        checkRequiredProperty(transactionNameTemplate, "transactionNameTemplate");
        checkRequiredProperty(transactionUserTemplate, "transactionUserTemplate");
        checkRequiredProperty(transactionCustomAttributeTemplates,
                "transactionCustomAttributeTemplates");
        checkRequiredProperty(enabledProperty, "enabledProperty");
        checkRequiredProperty(traceEntryEnabledProperty, "traceEntryEnabledProperty");
        checkRequiredProperty(version, "version");
        CapturePoint config = new CapturePoint(version);
        config.setClassName(className);
        config.setMethodName(methodName);
        config.setMethodParameterTypes(methodParameterTypes);
        config.setMethodReturnType(methodReturnType);
        config.setMethodModifiers(methodModifiers);
        config.setCaptureKind(captureKind);
        config.setMetricName(metricName);
        config.setTraceEntryTemplate(traceEntryTemplate);
        config.setTraceEntryStackThresholdMillis(traceEntryStackThresholdMillis);
        config.setTraceEntryCaptureSelfNested(traceEntryCaptureSelfNested);
        config.setTransactionType(transactionType);
        config.setTransactionNameTemplate(transactionNameTemplate);
        config.setTransactionUserTemplate(transactionUserTemplate);
        config.setTransactionCustomAttributeTemplates(transactionCustomAttributeTemplates);
        config.setTraceStoreThresholdMillis(traceStoreThresholdMillis);
        config.setEnabledProperty(enabledProperty);
        config.setTraceEntryEnabledProperty(traceEntryEnabledProperty);
        return config;
    }

    public enum MethodModifier {
        PUBLIC, PRIVATE, PROTECTED, PACKAGE_PRIVATE, STATIC, NOT_STATIC, ABSTRACT;
    }

    public static enum CaptureKind {
        METRIC, TRACE_ENTRY, TRANSACTION, OTHER
    }
}