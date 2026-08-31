package com.treinoapp.api.dto.auth;

public record AuthResponseDTO(
        String token,
        String memberId,
        String name,
        String email,
        String role
) {
}
