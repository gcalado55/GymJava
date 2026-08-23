package com.treinoapp.api.exception;

public abstract class NotFoundException extends RuntimeException {
    protected NotFoundException(String message){
        super(message);
    }
}
