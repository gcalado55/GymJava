package com.treinoapp.api.repository;

import com.treinoapp.api.model.ItemExercicio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ItemExercicioRepository extends JpaRepository<ItemExercicio, UUID> {
}