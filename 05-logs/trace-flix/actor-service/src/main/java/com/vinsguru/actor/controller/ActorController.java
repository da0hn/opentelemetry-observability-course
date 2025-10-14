package com.vinsguru.actor.controller;

import com.vinsguru.actor.dto.ActorDto;
import com.vinsguru.actor.exception.ActorNotFoundException;
import com.vinsguru.actor.service.ActorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ActorController {

    private static final Logger log = LoggerFactory.getLogger(ActorController.class);

    private final ActorService actorService;

    public ActorController(ActorService actorService) {
        this.actorService = actorService;
    }

    @GetMapping("/api/actors/{id}")
    public ResponseEntity<ActorDto> getActor(@PathVariable Integer id) {
        log.info("request received for actor id: {}", id);
        return this.actorService.getActor(id)
                                .map(ResponseEntity::ok)
                                .orElseThrow(() -> new ActorNotFoundException(id));
    }

}
