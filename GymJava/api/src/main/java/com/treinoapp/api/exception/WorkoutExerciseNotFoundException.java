package com.treinoapp.api.exception;

import java.util.UUID;

public class WorkoutExerciseNotFoundException extends NotFoundException {
    public WorkoutExerciseNotFoundException(UUID id) {
        super("Workout exercise not found: " + id);
    }
}