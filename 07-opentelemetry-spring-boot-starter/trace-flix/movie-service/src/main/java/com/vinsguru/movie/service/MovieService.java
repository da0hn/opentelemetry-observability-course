package com.vinsguru.movie.service;

import com.vinsguru.movie.client.ActorClient;
import com.vinsguru.movie.client.ReviewClient;
import com.vinsguru.movie.dto.MovieDto;
import com.vinsguru.movie.entity.Movie;
import com.vinsguru.movie.mapper.EntityDtoMapper;
import com.vinsguru.movie.repository.MovieRepository;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Context;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Gatherers;

@Service
public class MovieService {

    private final MovieRepository repository;
    private final ActorClient actorClient;
    private final ReviewClient reviewClient;

    public MovieService(MovieRepository repository, ActorClient actorClient, ReviewClient reviewClient) {
        this.repository = repository;
        this.actorClient = actorClient;
        this.reviewClient = reviewClient;
    }

    @WithSpan("Fetching movie details")
    public Optional<MovieDto> getMovie(Integer movieId) {
        return this.repository.findById(movieId)
                              .map(this::buildDto);
    }

    private MovieDto buildDto(Movie movie) {
        var span = Span.current();
        var reviews = this.reviewClient.getReviews(movie.getId());
        span.addEvent("Fetching actors concurrently");
        var actors = movie.getActorIds()
                          .stream()
                          //.map(this.actorClient::getActor) // intentional sequential calls
                          .gather(Gatherers.mapConcurrent(5, Context.current().wrapFunction(this.actorClient::getActor)))
                          .filter(Objects::nonNull)
                          .toList();
        return EntityDtoMapper.toDto(movie, actors, reviews);
    }

}
