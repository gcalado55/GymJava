package com.treinoapp.api.exception;

import java.util.UUID;

public class WorkoutNotFoundException extends NotFoundException {
    public WorkoutNotFoundException(UUID id) {
        super("Workout not found: " + id);
    }
}