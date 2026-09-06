package com.treinoapp.api.dto;

import java.util.UUID;

public record ExerciseVolumeDTO(UUID exerciseId, String exerciseName, String muscleGroup, Double volumeKg) {
}