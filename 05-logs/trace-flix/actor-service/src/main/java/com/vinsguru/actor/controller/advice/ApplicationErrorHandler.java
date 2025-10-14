package com.vinsguru.actor.controller.advice;

import com.vinsguru.actor.exception.ActorNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ApplicationErrorHandler {

    private static final Logger log = LoggerFactory.getLogger(ApplicationErrorHandler.class);

    @ExceptionHandler(ActorNotFoundException.class)
    public ProblemDetail handle(ActorNotFoundException exception){
        log.error("actor not found", exception);
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handle(Exception exception){
        log.error("unhandled error", exception);
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
    }

}
