package com.backend.thesis.domain.dto;

public class DatasetInfoDto {
    private Long idDataset;
    private String datasetName;
    private String columnName;
    private Long rowsCount;
    private String frequencyType;

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

    public void setColumnName(final String columnName) {
        this.columnName = columnName;
    }

    public String getFrequencyType() {
        return this.frequencyType;
    }

    public void setIdDataset(final Long idDataset) {
        this.idDataset = idDataset;
    }

    public void setDatasetName(final String datasetName) {
        this.datasetName = datasetName;
    }

    public void setRowsCount(final Long rowsCount) {
        this.rowsCount = rowsCount;
    }

    public void setFrequencyType(final String frequencyType) {
        this.frequencyType = frequencyType;
    }
}
