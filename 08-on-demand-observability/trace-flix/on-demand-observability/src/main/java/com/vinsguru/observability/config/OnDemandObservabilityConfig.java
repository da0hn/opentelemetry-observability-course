package com.vinsguru.observability.config;

import com.vinsguru.observability.otel.OnDemandBaggagePropagator;
import com.vinsguru.observability.otel.OnDemandTraceSampler;
import com.vinsguru.observability.web.BaggageMdcBridgeFilter;
import io.opentelemetry.api.baggage.propagation.W3CBaggagePropagator;
import io.opentelemetry.context.propagation.TextMapPropagator;
import io.opentelemetry.sdk.autoconfigure.spi.AutoConfigurationCustomizerProvider;
import io.opentelemetry.sdk.autoconfigure.spi.ConfigProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OnDemandObservabilityConfig {

    @Bean
    public BaggageMdcBridgeFilter baggageMdcBridgeFilter() {
        return new BaggageMdcBridgeFilter();
    }

    @Bean
    public OnDemandLogFilterRegistrar onDemandLogFilterRegistrar() {
        return new OnDemandLogFilterRegistrar();
    }

    @Bean
    public AutoConfigurationCustomizerProvider autoConfigurationCustomizerProvider() {
        return autoConfiguration -> autoConfiguration
                .addSamplerCustomizer(((sampler, configProperties) -> new OnDemandTraceSampler(sampler)))
                .addPropagatorCustomizer(this::customizePropagator);
    }

    // W3CTraceContextPropagator
    // W3CBaggagePropagator
    private TextMapPropagator customizePropagator(TextMapPropagator propagator, ConfigProperties configProperties) {
        return switch (propagator) {
            case W3CBaggagePropagator w3CBaggagePropagator -> TextMapPropagator.composite(new OnDemandBaggagePropagator(), w3CBaggagePropagator);
            case null, default -> propagator;
        };
    }

}
