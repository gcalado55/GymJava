package com.treinoapp.api.dto;

public record WorkoutExerciseDTO(
        String exerciseName,
        Integer sets,
        Integer reps,
        Double weightKg,
        String notes,
        String technique,
        String executionGuidance
) {
}