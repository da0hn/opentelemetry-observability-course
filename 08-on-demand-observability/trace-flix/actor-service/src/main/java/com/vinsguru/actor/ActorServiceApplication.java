package com.vinsguru.actor;

import com.vinsguru.observability.EnableOnDemandObservability;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableOnDemandObservability
public class ActorServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ActorServiceApplication.class, args);
    }

}
