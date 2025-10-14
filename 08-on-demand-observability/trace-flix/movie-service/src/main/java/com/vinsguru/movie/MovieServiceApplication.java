package com.vinsguru.movie;

import com.vinsguru.observability.EnableOnDemandObservability;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableOnDemandObservability
public class MovieServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MovieServiceApplication.class, args);
	}

}
