package com.treinoapp.api.dto;

import java.util.List;

public record ProgressOverviewDTO(
        List<MonthlyVolumeDTO> monthlyVolume,
        List<ExerciseProgressSummaryDTO> exercises
) {
}