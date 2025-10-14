package com.vinsguru.actor.exception;

public class ActorNotFoundException extends RuntimeException {

  private static final String MESSAGE = "Actor [id=%d] is not found";

  public ActorNotFoundException(Integer id) {
    super(MESSAGE.formatted(id));
  }

}
