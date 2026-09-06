package com.treinoapp.api.dto;

import java.util.List;

public record ProgressOverviewDTO(
        List<WeeklyVolumeDTO> weeklyVolume,
        List<ExerciseProgressSummaryDTO> exercises
) {
}