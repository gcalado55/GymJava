package com.treinoapp.api.exception;

import java.util.UUID;

public class ExerciseNotFoundException extends NotFoundException {
    public ExerciseNotFoundException(UUID id) {
        super("Exercise not found: " + id);
    }
}