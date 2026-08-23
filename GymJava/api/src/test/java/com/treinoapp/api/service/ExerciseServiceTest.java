package com.treinoapp.api.service;

import com.treinoapp.api.model.Exercise;
import com.treinoapp.api.repository.ExerciseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExerciseServiceTest {

    @Mock
    private ExerciseRepository exerciseRepository;

    @InjectMocks
    private ExerciseService exerciseService;

    @Test
    void shouldCreateExercise() {
        when(exerciseRepository.save(any(Exercise.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Exercise result = exerciseService.create("Supino Reto", "Chest");

        assertEquals("Supino Reto", result.getName());
        assertEquals("Chest", result.getMuscleGroup());
        verify(exerciseRepository).save(any(Exercise.class));
    }

    @Test
    void shouldFindAllExercises() {
        Exercise exercise1 = new Exercise();
        exercise1.setName("Supino Reto");
        Exercise exercise2 = new Exercise();
        exercise2.setName("Agachamento");
        when(exerciseRepository.findAll()).thenReturn(List.of(exercise1, exercise2));

        List<Exercise> result = exerciseService.findAll();

        assertEquals(2, result.size());
    }

    @Test
    void shouldFindExerciseById() {
        UUID id = UUID.randomUUID();
        Exercise fakeExercise = new Exercise();
        fakeExercise.setId(id);
        when(exerciseRepository.findById(id)).thenReturn(Optional.of(fakeExercise));

        Exercise result = exerciseService.findById(id);

        assertEquals(id, result.getId());
    }

    @Test
    void shouldThrowExceptionWhenExerciseNotFound() {
        UUID id = UUID.randomUUID();
        when(exerciseRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> exerciseService.findById(id));
    }

}