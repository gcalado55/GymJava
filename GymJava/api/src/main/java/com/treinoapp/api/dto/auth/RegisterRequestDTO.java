package com.treinoapp.api.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequestDTO(
        @NotBlank(message = "name is required") String name,
        @NotBlank @Email(message = "invalid email") String email,
        @NotBlank(message = "password is required") String password
) {
}
