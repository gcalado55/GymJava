package com.treinoapp.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record AddSetRequestDTO(
        @NotNull @Positive(message = "reps must be positive") Integer reps,
        @NotNull @PositiveOrZero(message = "weightKg must be positive or zero") Double weightKg
) {}