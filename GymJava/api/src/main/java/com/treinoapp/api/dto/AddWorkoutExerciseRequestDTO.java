package com.treinoapp.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.UUID;

public record AddWorkoutExerciseRequestDTO(
        @NotNull(message = "exerciseId is required") UUID exerciseId,
        @NotNull @Positive(message = "sets must be positive") Integer sets,
        @NotNull @Positive(message = "reps must be positive") Integer reps,
        @PositiveOrZero(message = "weightKg must be positive or zero") Double weightKg,
        String technique,
        String notes
) {
}