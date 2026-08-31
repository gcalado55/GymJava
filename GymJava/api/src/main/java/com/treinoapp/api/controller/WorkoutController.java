package com.treinoapp.api.controller;

import com.treinoapp.api.dto.*;
import com.treinoapp.api.model.Workout;
import com.treinoapp.api.service.WorkoutService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import java.util.UUID;

import java.util.List;

@RestController
@RequestMapping("/api/workouts")
public class WorkoutController {

    private final WorkoutService workoutService;

    public WorkoutController(WorkoutService workoutService) {
        this.workoutService = workoutService;
    }

    @PostMapping
    public ResponseEntity<Workout> create(@AuthenticationPrincipal UUID memberId, @Valid @RequestBody WorkoutRequestDTO dto) {
        Workout workout = workoutService.create(dto.name(), memberId, dto.isTemplate());
        return ResponseEntity.ok(workout);
    }

    @PostMapping("/{workoutId}/exercises")
    public ResponseEntity<Workout> addExercise(@PathVariable UUID workoutId,
                                               @Valid @RequestBody
                                               AddWorkoutExerciseRequestDTO dto) {
        Workout workout = workoutService.addExercise(
                workoutId, dto.exerciseId(),
                dto.technique(),
                dto.notes()
        );
        return ResponseEntity.ok(workout);
    }

    @DeleteMapping("/{workoutId}/exercises/{workoutExerciseId}")
    public ResponseEntity<Workout> removeExercise(@PathVariable UUID workoutId,
                                                  @PathVariable UUID workoutExerciseId) {
        Workout workout = workoutService.removeExercise(workoutId, workoutExerciseId);
        return ResponseEntity.ok(workout);
    }

    @PostMapping("/{workoutId}/exercises/{workoutExerciseId}/sets")
    public ResponseEntity<Workout> addSet(@PathVariable UUID workoutId,
                                          @PathVariable UUID workoutExerciseId,
                                          @Valid @RequestBody AddSetRequestDTO dto) {
        Workout workout = workoutService.addSet(workoutId, workoutExerciseId, dto.reps(), dto.weightKg());
        return ResponseEntity.ok(workout);
    }

    @PatchMapping("/{workoutId}/exercises/{workoutExerciseId}/notes")
    public ResponseEntity<Workout> updateExerciseNote(@PathVariable UUID workoutId,
                                                      @PathVariable UUID workoutExerciseId,
                                                      @Valid @RequestBody UpdateNoteRequestDTO dto) {
        Workout workout = workoutService.updateExerciseNote(workoutExerciseId, dto.note());
        return ResponseEntity.ok(workout);
    }

    @PatchMapping("/{workoutId}/complete")
    public ResponseEntity<Workout> completeWorkout(@PathVariable UUID workoutId) {
        Workout workout = workoutService.completeWorkout(workoutId);
        return ResponseEntity.ok(workout);
    }

    @PostMapping("/{templateId}/start")
    public ResponseEntity<Workout> startFromTemplate(@PathVariable UUID templateId, @AuthenticationPrincipal UUID memberId) {
        Workout workout = workoutService.startFromTemplate(templateId, memberId);
        return ResponseEntity.ok(workout);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkoutResponseDTO> findByIdFormatted(@PathVariable UUID id) {
        WorkoutResponseDTO dto = workoutService.findByIdFormatted(id);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkout(@PathVariable UUID id) {
        workoutService.deleteWorkout(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<WorkoutSummaryDTO>> findAll(
            @AuthenticationPrincipal UUID memberId,
            @RequestParam(required = false) Boolean isTemplate) {
        return ResponseEntity.ok(workoutService.findAll(memberId, isTemplate));
    }

}