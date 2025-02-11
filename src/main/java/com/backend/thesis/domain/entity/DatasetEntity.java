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

    @Column(name = "rows_count", nullable = false)
    private Long rowsCount;

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
            final Long rowsCount,
            final String fileName,
            final LocalDateTime startAt,
            final LocalDateTime endAt
    ) {
        this.idUser = idUser;
        this.idFrequency = idFrequency;
        this.datasetName = datasetName;
        this.columnName = columnName;
        this.rowsCount = rowsCount;
        this.fileName = fileName;
        this.startAt = startAt;
        this.endAt = endAt;
    }

    public Long getIdUser() {
        return this.idUser;
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

    public String getColumnName() {
        return this.columnName;
    }

    public Long getRowsCount() {
        return this.rowsCount;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setColumnName(final String columnName) {
        this.columnName = columnName;
    }

    public void setRowsCount(final Long rowsCount) {
        this.rowsCount = rowsCount;
    }

    public void setStartAt(final LocalDateTime startAt) {
        this.startAt = startAt;
    }

    public void setEndAt(final LocalDateTime endAt) {
        this.endAt = endAt;
    }
}
