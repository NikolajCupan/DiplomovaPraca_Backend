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

    @Column(name = "dataset_name", nullable = false)
    private String datasetName;

    @Column(name = "column_name", nullable = false)
    private String columnName;

    @Column(name = "file_name", nullable = false, unique = true)
    private String fileName;

    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    @Column(name = "end_at", nullable = false)
    private LocalDateTime endAt;

    public DatasetEntity() {
    }

    public DatasetEntity(
            final Long idUser,
            final Long idFrequency,
            final String datasetName,
            final String columnName,
            final String fileName,
            final LocalDateTime startAt,
            final LocalDateTime endAt
    ) {
        this.idUser = idUser;
        this.idFrequency = idFrequency;
        this.datasetName = datasetName;
        this.columnName = columnName;
        this.fileName = fileName;
        this.startAt = startAt;
        this.endAt = endAt;
    }

    public Long getIdFrequency() {
        return this.idFrequency;
    }

    public Long getIdDataset() {
        return this.idDataset;
    }

    public String getDatasetName() {
        return this.datasetName;
    }
}
