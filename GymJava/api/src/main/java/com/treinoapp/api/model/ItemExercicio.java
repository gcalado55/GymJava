package com.treinoapp.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "item_exercicio")
@Getter
@Setter
@NoArgsConstructor
public class ItemExercicio {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "treino_id", nullable = false)
    private Treino treino;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercicio_id", nullable = false)
    private Exercicio exercicio;

    @Column(nullable = false)
    private Integer series;

    @Column(nullable = false)
    private Integer repeticoes;

    @Column(nullable = false)
    private Double cargaKg;

    @Column(nullable = false)
    private String tecnica = "NENHUMA_TECNICA";

    @Column(columnDefinition = "TEXT")
    private String anotacoes;

}