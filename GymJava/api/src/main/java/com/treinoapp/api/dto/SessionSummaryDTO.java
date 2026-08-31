package com.treinoapp.api.dto;

import java.time.Instant;

public record SessionSummaryDTO(
        Instant date,
        Double volumeKg,
        Integer totalReps,
        Double bestSetWeightKg,
        Integer bestSetReps
) {
}