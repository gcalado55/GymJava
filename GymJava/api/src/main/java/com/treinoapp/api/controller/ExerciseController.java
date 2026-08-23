package com.treinoapp.api.controller;

import com.treinoapp.api.dto.ExerciseRequestDTO;
import com.treinoapp.api.model.Exercise;
import com.treinoapp.api.service.ExerciseService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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
}
