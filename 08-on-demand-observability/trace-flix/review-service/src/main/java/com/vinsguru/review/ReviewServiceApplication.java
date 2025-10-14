package com.vinsguru.review;

import com.vinsguru.observability.EnableOnDemandObservability;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableOnDemandObservability
public class ReviewServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReviewServiceApplication.class, args);
    }

}
