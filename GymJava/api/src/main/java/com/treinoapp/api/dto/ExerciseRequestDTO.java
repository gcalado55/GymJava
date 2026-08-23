package com.treinoapp.api.dto;

import jakarta.validation.constraints.NotBlank;

public record ExerciseRequestDTO(
        @NotBlank(message = "name is required") String name,
        @NotBlank(message = "muscleGroup is required") String muscleGroup
) {
}