package com.vinsguru.movie.config;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.LongCounter;

public class MovieViewMetrics {

    private static final AttributeKey<Long> MOVIE_ID_KEY = AttributeKey.longKey("movie.id");
    private final LongCounter counter;

    public MovieViewMetrics(LongCounter counter) {
        this.counter = counter;
    }

    public void recordView(long movieId){
        this.counter.add(1, Attributes.of(MOVIE_ID_KEY, movieId));
    }

}
