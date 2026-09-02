package com.treinoapp.api.service;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProgressCalculator {

    public double sum(List<Double> values) {
        return values.stream().mapToDouble(Double::doubleValue).sum();
    }

    public double average(List<Double> values) {
        return values.isEmpty() ? 0.0 : sum(values) / values.size();
    }

    public double percentChange(double first, double last) {
        if (first == 0) return 0.0;
        return ((last - first) / first) * 100;
    }

    public double calculateOneRepMax(double weightKg, int reps) {
        if (reps == 0) return 0.0;
        if (reps == 1) return weightKg;
        return weightKg * (1.0 + (reps / 30.0));
    }
}