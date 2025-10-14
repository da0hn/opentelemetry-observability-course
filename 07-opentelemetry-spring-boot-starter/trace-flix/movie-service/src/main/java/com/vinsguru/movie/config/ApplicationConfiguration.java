package com.vinsguru.movie.config;

import com.vinsguru.movie.client.ActorClient;
import com.vinsguru.movie.client.ReviewClient;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.sdk.autoconfigure.spi.AutoConfigurationCustomizerProvider;
import io.opentelemetry.sdk.metrics.export.PeriodicMetricReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ApplicationConfiguration {

    @Bean
    public ActorClient actorClient(RestClient.Builder builder, @Value("${actor-service.url}") String baseUrl) {
        var client = builder.baseUrl(baseUrl).build();
        return new ActorClient(client);
    }

    @Bean
    public ReviewClient reviewClient(RestClient.Builder builder, @Value("${review-service.url}") String baseUrl) {
        var client = builder.baseUrl(baseUrl).build();
        return new ReviewClient(client);
    }

    @Bean
    public Meter openTelemetryMeter(OpenTelemetry openTelemetry) {
        return openTelemetry.getMeter("movie-service");
    }

    @Bean
    public MovieViewMetrics movieViewMetrics(Meter meter) {
        var counter = meter.counterBuilder("app.movie.view.count")
                           .setDescription("Total number of movie view")
                           .setUnit("{view}")
                           .build();
        return new MovieViewMetrics(counter);
    }

}
