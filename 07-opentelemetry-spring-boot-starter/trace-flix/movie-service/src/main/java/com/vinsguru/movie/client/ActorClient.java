package com.vinsguru.movie.client;

import com.vinsguru.movie.dto.ActorDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ProblemDetail;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

public class ActorClient {

    private static final Logger log = LoggerFactory.getLogger(ActorClient.class);
    private final RestClient restClient;

    public ActorClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public ActorDto getActor(Integer actorId) {
        log.info("calling actor-service for actor id: {}", actorId);
        try{
            return this.restClient.get()
                                  .uri("/{actorId}", actorId)
                                  .retrieve()
                                  .body(ActorDto.class);
        }catch (HttpClientErrorException exception){
            var problemDetail = exception.getResponseBodyAs(ProblemDetail.class);
            log.warn("unable to get actor info. http client error: {}", problemDetail);
            return null;
        }
    }

}
