package com.treinoapp.api.controller;

import com.treinoapp.api.dto.ExerciseRequestDTO;
import com.treinoapp.api.model.Exercise;
import com.treinoapp.api.service.ExerciseService;
import jakarta.validation.Valid;
import com.treinoapp.api.dto.ExerciseProgressDTO;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/exercises")
public class ExerciseController {

    private final ExerciseService exerciseService;

    public ExerciseController(ExerciseService exerciseService){
        this.exerciseService = exerciseService;
    }

    @PostMapping
    public ResponseEntity<Exercise> create(@Valid @RequestBody ExerciseRequestDTO dto){
        Exercise exercise = exerciseService.create(dto.name(), dto.muscleGroup());
        return ResponseEntity.ok(exercise);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Exercise> findById(@PathVariable UUID id){
        Exercise exercise = exerciseService.findById(id);
        return ResponseEntity.ok(exercise);
    }

    @GetMapping
    public ResponseEntity<List<Exercise>> findAll(){
        List<Exercise> exercises = exerciseService.findAll();
        return ResponseEntity.ok(exercises);
    }

    @GetMapping("/{id}/progress")
    public ResponseEntity<ExerciseProgressDTO> getProgress(@PathVariable UUID id, @AuthenticationPrincipal UUID memberId) {
        return ResponseEntity.ok(exerciseService.progress(id, memberId));
    }

    @GetMapping("/{id}/previous-note")
    public ResponseEntity<com.treinoapp.api.dto.PreviousNoteDTO> getPreviousNote(@PathVariable UUID id, @AuthenticationPrincipal UUID memberId) {
        return exerciseService.getPreviousNote(id, memberId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }
}
