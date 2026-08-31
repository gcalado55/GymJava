package com.treinoapp.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.util.UUID;

public record CreateGoalRequestDTO(
        @NotNull(message = "exerciseId is required") UUID exerciseId,
        @NotNull @Positive(message = "targetWeightKg must be positive") Double targetWeightKg,
        @NotNull @Positive(message = "targetReps must be positive") Integer targetReps,
        LocalDate targetDate
) {
}