package com.tcell_spring.restservice.domain.exception;

public class SameEntityExistException extends RuntimeException{
    public SameEntityExistException(String message) {
        super(message);
    }
}
