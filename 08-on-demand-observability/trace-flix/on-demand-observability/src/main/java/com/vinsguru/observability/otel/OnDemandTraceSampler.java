package com.vinsguru.observability.otel;

import com.vinsguru.observability.DebugRequestKeys;
import io.opentelemetry.api.baggage.Baggage;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.context.Context;
import io.opentelemetry.sdk.trace.data.LinkData;
import io.opentelemetry.sdk.trace.samplers.Sampler;
import io.opentelemetry.sdk.trace.samplers.SamplingResult;

import java.util.List;
import java.util.Optional;

public class OnDemandTraceSampler implements Sampler {

    private final Sampler delegate;

    public OnDemandTraceSampler(Sampler delegate) {
        this.delegate = delegate;
    }

    @Override
    public SamplingResult shouldSample(Context parentContext, String traceId, String name, SpanKind spanKind, Attributes attributes, List<LinkData> parentLinks) {
        var baggage = Baggage.fromContext(parentContext);
        return Optional.ofNullable(baggage.getEntryValue(DebugRequestKeys.BAGGAGE))
                       .map(requestId -> SamplingResult.recordAndSample())
                       .orElseGet(() -> this.delegate.shouldSample(parentContext, traceId, name, spanKind, attributes, parentLinks));
    }

    @Override
    public String getDescription() {
        return "OnDemandTraceSampler";
    }
}
