package com.treinoapp.api.dto;

import java.util.List;

public record WorkoutResponseDTO(
        String name,
        String memberName,
        List<WorkoutExerciseDTO> exercises
) {
}