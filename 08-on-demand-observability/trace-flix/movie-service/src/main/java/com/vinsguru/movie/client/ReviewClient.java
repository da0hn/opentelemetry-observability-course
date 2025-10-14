package com.vinsguru.movie.client;

import com.vinsguru.movie.dto.ReviewDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ProblemDetail;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;

public class ReviewClient {

    private static final Logger log = LoggerFactory.getLogger(ReviewClient.class);
    private final RestClient restClient;

    public ReviewClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public List<ReviewDto> getReviews(Integer movieId) {
        log.info("calling review-service for movie id: {}", movieId);
        try{
            return this.restClient.get()
                                  .uri(b -> b.queryParam("movieId", movieId).build())
                                  .retrieve()
                                  .body(new ParameterizedTypeReference<List<ReviewDto>>() {
                                  });
        }catch (HttpClientErrorException exception){
            var problemDetail = exception.getResponseBodyAs(ProblemDetail.class);
            log.warn("unable to get review info. http client error: {}", problemDetail);
            return Collections.emptyList();
        }
    }

}
