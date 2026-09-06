package com.treinoapp.api.dto;

import java.time.Instant;

public record ProgressPointDTO(Instant date, Double volumeKg) {
}