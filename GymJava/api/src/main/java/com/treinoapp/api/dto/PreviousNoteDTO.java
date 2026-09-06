package com.treinoapp.api.dto;

import java.time.Instant;

public record PreviousNoteDTO(
        Instant date,
        long daysElapsed,
        String note,
        Double bestWeightKg,
        Integer bestReps,
        Integer setsCount
) {
}
