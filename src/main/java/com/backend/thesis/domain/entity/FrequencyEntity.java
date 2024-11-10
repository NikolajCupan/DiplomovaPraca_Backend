package com.backend.thesis.domain.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "frequency")
public class FrequencyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_frequency", nullable = false, unique = true)
    private Long idFrequency;

    @Column(name = "frequency_type", nullable = false, unique = true)
    private String frequencyType;

    public Long getIdFrequency() {
        return idFrequency;
    }
}
