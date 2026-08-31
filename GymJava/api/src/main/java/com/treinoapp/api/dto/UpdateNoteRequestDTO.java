package com.treinoapp.api.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateNoteRequestDTO(
        @NotNull(message = "note cannot be null") String note
) {
}
