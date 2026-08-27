package com.treinoapp.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record WorkoutRequestDTO(
        @NotBlank(message = "name is required") String name,
        @NotNull(message = "memberId is required") UUID memberId
    ){
}
