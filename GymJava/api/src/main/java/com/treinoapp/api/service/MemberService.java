package com.treinoapp.api.service;

import com.treinoapp.api.dto.*;
import com.treinoapp.api.exception.MemberNotFoundException;
import com.treinoapp.api.model.Exercise;
import com.treinoapp.api.model.Member;
import com.treinoapp.api.model.Workout;
import com.treinoapp.api.model.WorkoutSet;
import com.treinoapp.api.repository.MemberRepository;
import com.treinoapp.api.repository.WorkoutRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final WorkoutRepository workoutRepository;
    private final ProgressCalculator progressCalculator;

    public MemberService(MemberRepository memberRepository,
                         WorkoutRepository workoutRepository,
                         ProgressCalculator progressCalculator) {
        this.memberRepository = memberRepository;
        this.workoutRepository = workoutRepository;
        this.progressCalculator = progressCalculator;
    }

    public Member create(String name, String email) {
        Member member = new Member();
        member.setName(name);
        member.setEmail(email);
        return memberRepository.save(member);
    }

    public Member update(UUID id, String name, String email) {
        Member member = findById(id);
        member.setName(name);
        member.setEmail(email);
        return memberRepository.save(member);
    }

    public Member findById(UUID id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException(id));
    }

    public DashboardStatsDTO dashboardStats(UUID memberId) {
        Instant cutoff30 = Instant.now().minus(30, ChronoUnit.DAYS);
        Instant cutoff7 = Instant.now().minus(7, ChronoUnit.DAYS);

        record TimedSet(WorkoutSet set, Instant date, Exercise exercise) {
        }

        List<Workout> workouts = workoutRepository.findByMemberIdOrderByCreatedAtDesc(memberId).stream()
                .filter(w -> !w.isTemplate())
                .filter(w -> !w.getCreatedAt().isBefore(cutoff30))
                .toList();

        List<TimedSet> allSets = workouts.stream()
                .flatMap(w -> w.getExercises().stream()
                        .flatMap(we -> we.getSets().stream()
                                .map(s -> new TimedSet(s, w.getCreatedAt(), we.getExercise()))))
                .toList();

        double totalVolume = allSets.size(); // Total Sets instead of load
        double averageLoad = progressCalculator.average(
                allSets.stream().map(ts -> ts.set().getWeightKg()).toList());
        int setsThisWeek = (int) allSets.stream().filter(ts -> !ts.date().isBefore(cutoff7)).count();

        List<PriorityExerciseDTO> priority = allSets.stream()
                .collect(Collectors.groupingBy(TimedSet::exercise))
                .entrySet().stream()
                .map(entry -> {
                    List<TimedSet> ts = entry.getValue().stream()
                            .sorted(Comparator.comparing(TimedSet::date)).toList();
                    double volume = ts.size(); // Total Sets
                    double first = ts.stream().filter(t -> t.date().equals(ts.get(0).date())).count(); // First session sets
                    double last = ts.stream().filter(t -> t.date().equals(ts.get(ts.size() - 1).date())).count(); // Last session sets
                    double pct = progressCalculator.percentChange(first, last);
                    List<Double> points = ts.stream()
                            .collect(Collectors.groupingBy(TimedSet::date))
                            .entrySet().stream().sorted(Map.Entry.comparingByKey())
                            .map(e -> (double) e.getValue().size())
                            .toList(); // Sets per session
                    return new PriorityExerciseDTO(entry.getKey().getName(), entry.getKey().getMuscleGroup(),
                            volume, pct, points);
                })
                .sorted(Comparator.comparingDouble(PriorityExerciseDTO::volumeKg).reversed())
                .limit(3)
                .toList();

        double overallProgress = priority.isEmpty() ? 0.0 :
                progressCalculator.average(priority.stream().map(PriorityExerciseDTO::progressPct).toList());

        return new DashboardStatsDTO(workouts.size(), totalVolume, averageLoad, overallProgress, priority, setsThisWeek);
    }

    public ProgressOverviewDTO progressOverview(UUID memberId) {
        Instant cutoff = Instant.now().minus(90, ChronoUnit.DAYS);

        record TimedSet(WorkoutSet set, Instant date, Exercise exercise) {
        }

        List<Workout> workouts = workoutRepository.findByMemberIdOrderByCreatedAtDesc(memberId).stream()
                .filter(w -> !w.isTemplate())
                .filter(w -> !w.getCreatedAt().isBefore(cutoff))
                .toList();

        List<TimedSet> allSets = workouts.stream()
                .flatMap(w -> w.getExercises().stream()
                        .flatMap(we -> we.getSets().stream()
                                .map(s -> new TimedSet(s, w.getCreatedAt(), we.getExercise()))))
                .toList();

        List<MonthlyVolumeDTO> monthlyVolume = allSets.stream()
                .collect(Collectors.groupingBy(ts -> YearMonth.from(ts.date().atZone(ZoneOffset.UTC)).toString()))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> new MonthlyVolumeDTO(e.getKey(), (double) e.getValue().size())) // Sets instead of load
                .toList();

        List<ExerciseProgressSummaryDTO> exercises = allSets.stream()
                .collect(Collectors.groupingBy(TimedSet::exercise))
                .entrySet().stream()
                .map(entry -> {
                    List<TimedSet> ts = entry.getValue().stream()
                            .sorted(Comparator.comparing(TimedSet::date)).toList();
                    double first = ts.stream().filter(t -> t.date().equals(ts.get(0).date())).count();
                    double last = ts.stream().filter(t -> t.date().equals(ts.get(ts.size() - 1).date())).count();
                    double pct = progressCalculator.percentChange(first, last);
                    List<Double> points = ts.stream()
                            .collect(Collectors.groupingBy(TimedSet::date))
                            .entrySet().stream().sorted(Map.Entry.comparingByKey())
                            .map(e -> (double) e.getValue().size())
                            .toList();
                    return new ExerciseProgressSummaryDTO(entry.getKey().getName(), pct, points);
                })
                .toList();

        return new ProgressOverviewDTO(monthlyVolume, exercises);
    }
}