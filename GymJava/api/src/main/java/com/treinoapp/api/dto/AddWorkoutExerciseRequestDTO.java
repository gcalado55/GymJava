package com.treinoapp.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.UUID;

public record AddWorkoutExerciseRequestDTO(
        @NotNull(message = "exerciseId is required") UUID exerciseId,
        String technique,
        String notes
) {
}