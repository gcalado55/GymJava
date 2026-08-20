package com.treinoapp.api.controller;

import com.treinoapp.api.dto.AddWorkoutExerciseRequestDTO;
import com.treinoapp.api.dto.WorkoutRequestDTO;
import com.treinoapp.api.dto.WorkoutResponseDTO;
import com.treinoapp.api.model.Workout;
import com.treinoapp.api.service.WorkoutService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/workouts")
public class WorkoutController {

    private final WorkoutService workoutService;

    public WorkoutController(WorkoutService workoutService) {
        this.workoutService = workoutService;
    }

    @PostMapping
    public ResponseEntity<Workout> create(@Valid @RequestBody WorkoutRequestDTO dto) {
        Workout workout = workoutService.create(dto.name(), dto.memberId());
        return ResponseEntity.ok(workout);
    }

    @PostMapping("/{workoutId}/exercises")
    public ResponseEntity<Workout> addExercise(@PathVariable UUID workoutId,
                                               @Valid @RequestBody AddWorkoutExerciseRequestDTO dto) {
        Workout workout = workoutService.addExercise(
                workoutId,
                dto.exerciseId(),
                dto.sets(),
                dto.reps(),
                dto.weightKg(),
                dto.technique(),
                dto.notes()
        );
        return ResponseEntity.ok(workout);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkoutResponseDTO> findByIdFormatted(@PathVariable UUID id) {
        WorkoutResponseDTO dto = workoutService.findByIdFormatted(id);
        return ResponseEntity.ok(dto);
    }

}