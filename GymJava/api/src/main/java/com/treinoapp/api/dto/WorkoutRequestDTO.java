package com.treinoapp.api.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record WorkoutRequestDTO(
        @NotBlank(message = "name is required") String name,
        @NotBlank(message = "memberId is required") UUID memberId
    ){
}
