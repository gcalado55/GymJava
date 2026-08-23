package com.treinoapp.api.service;

import com.treinoapp.api.dto.WorkoutResponseDTO;
import com.treinoapp.api.model.Exercise;
import com.treinoapp.api.model.Member;
import com.treinoapp.api.model.Workout;
import com.treinoapp.api.model.WorkoutExercise;
import com.treinoapp.api.repository.WorkoutRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkoutServiceTest {

    @Mock
    private WorkoutRepository workoutRepository;

    @Mock
    private MemberService memberService;

    @Mock
    private ExerciseService exerciseService;

    @InjectMocks
    private WorkoutService workoutService;

    @Test
    void shouldCreateWorkoutWhenMemberExists() {
        UUID memberId = UUID.randomUUID();
        Member fakeMember = new Member();
        fakeMember.setId(memberId);
        fakeMember.setName("Gabriel");

        when(memberService.findById(memberId)).thenReturn(fakeMember);
        when(workoutRepository.save(any(Workout.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Workout result = workoutService.create("Push Day", memberId);

        assertEquals("Push Day", result.getName());
        assertEquals(fakeMember, result.getMember());
        verify(workoutRepository).save(any(Workout.class));
    }

    @Test
    void shouldThrowExceptionWhenCreatingWorkoutWithNonexistentMember() {
        UUID memberId = UUID.randomUUID();
        when(memberService.findById(memberId)).thenThrow(new IllegalArgumentException("Member not found: " + memberId));

        assertThrows(IllegalArgumentException.class, () -> workoutService.create("Push Day", memberId));
    }

    @Test
    void shouldThrowExceptionWhenWorkoutNotFound() {
        UUID workoutId = UUID.randomUUID();
        when(workoutRepository.findById(workoutId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> workoutService.findById(workoutId));
    }

    @Test
    void shouldAddExerciseToWorkout() {
        UUID workoutId = UUID.randomUUID();
        UUID exerciseId = UUID.randomUUID();

        Workout fakeWorkout = new Workout();
        fakeWorkout.setId(workoutId);
        fakeWorkout.setName("Push Day");

        Exercise fakeExercise = new Exercise();
        fakeExercise.setId(exerciseId);
        fakeExercise.setName("Supino Reto");

        when(workoutRepository.findById(workoutId)).thenReturn(Optional.of(fakeWorkout));
        when(exerciseService.findById(exerciseId)).thenReturn(fakeExercise);
        when(workoutRepository.save(any(Workout.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Workout result = workoutService.addExercise(workoutId, exerciseId, 4, 10, 35.0, "DROP_SET", "Foco na execucao lenta");

        assertEquals(1, result.getExercises().size());
        assertEquals("DROP_SET", result.getExercises().get(0).getTechnique());
    }

    @Test
    void shouldReturnFormattedWorkoutWithExecutionGuidance() {
        UUID workoutId = UUID.randomUUID();

        Member fakeMember = new Member();
        fakeMember.setName("Gabriel");

        Exercise fakeExercise = new Exercise();
        fakeExercise.setName("Supino Reto");

        WorkoutExercise workoutExercise = new WorkoutExercise();
        workoutExercise.setExercise(fakeExercise);
        workoutExercise.setSets(4);
        workoutExercise.setReps(10);
        workoutExercise.setWeightKg(35.0);
        workoutExercise.setTechnique("DROP_SET");
        workoutExercise.setNotes("Foco na execucao lenta");

        Workout fakeWorkout = new Workout();
        fakeWorkout.setId(workoutId);
        fakeWorkout.setName("Push Day");
        fakeWorkout.setMember(fakeMember);
        fakeWorkout.getExercises().add(workoutExercise);

        when(workoutRepository.findById(workoutId)).thenReturn(Optional.of(fakeWorkout));

        WorkoutResponseDTO result = workoutService.findByIdFormatted(workoutId);

        assertEquals("Push Day", result.name());
        assertEquals("Gabriel", result.memberName());
        assertEquals(1, result.exercises().size());
        assertEquals("Drop Set", result.exercises().get(0).executionGuidance());
    }

}