package com.backend.thesis.domain.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "dataset")
public class DatasetEntity {
    @JoinColumn(name = "id_user", referencedColumnName = "id_user")
    @Column(name = "id_user", nullable = false)
    private Long idUser;

    @JoinColumn(name = "id_frequency", referencedColumnName = "id_frequency")
    @Column(name = "id_frequency", nullable = false)
    private Long idFrequency;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_dataset", nullable = false, unique = true)
    private Long idDataset;

    @Column(name = "md5", nullable = false, unique = true)
    private String md5;

    @Column(name = "file_name", nullable = false, unique = true)
    private String fileName;

    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;
}
