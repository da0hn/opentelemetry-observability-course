package com.vinsguru.observability.otel;

import com.vinsguru.observability.DebugRequestKeys;
import io.opentelemetry.api.baggage.Baggage;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.propagation.TextMapGetter;
import io.opentelemetry.context.propagation.TextMapPropagator;
import io.opentelemetry.context.propagation.TextMapSetter;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class OnDemandBaggagePropagator implements TextMapPropagator {

    private static final List<String> FIELDS = List.of(DebugRequestKeys.HEADER);

    @Override
    public Collection<String> fields() {
        return FIELDS;
    }

    @Override
    public <C> void inject(Context context, C carrier, TextMapSetter<C> setter) {
        // not required in our case
    }

    @Override // Carrier: The object containing the headers. For ex: http request
    public <C> Context extract(Context context, C carrier, TextMapGetter<C> getter) {
        return Optional.ofNullable(getter.get(carrier, DebugRequestKeys.HEADER))
                       .filter(Predicate.not(String::isEmpty))
                       .map(this::toBaggage)
                       .map(context::with)
                       .orElse(context);
    }

    private Baggage toBaggage(String requestId) {
        return Baggage.current()
                      .toBuilder()
                      .put(DebugRequestKeys.BAGGAGE, requestId)
                      .build();
    }

}
