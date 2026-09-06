package com.treinoapp.api.dto;

import java.time.Instant;
import java.util.UUID;

public record WorkoutSummaryDTO(
        UUID id,
        String name,
        String memberName,
        Instant createdAt,
        String status,
        boolean isTemplate,
        int exerciseCount
) {}