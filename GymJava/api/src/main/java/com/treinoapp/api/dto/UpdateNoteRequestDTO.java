package com.treinoapp.api.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateNoteRequestDTO(
        String note,
        String logNotes
) {
}
