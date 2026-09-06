package com.treinoapp.api.dto;

import java.time.Instant;
import java.util.UUID;

public record SessionVolumeDTO(UUID exerciseId, Instant date, Double volumeKg) {
}