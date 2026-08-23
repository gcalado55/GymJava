package com.treinoapp.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record MemberRequestDTO(
        @NotBlank(message = "name is required") String name,
        @NotBlank @Email(message = "invalid email") String email
) {
}