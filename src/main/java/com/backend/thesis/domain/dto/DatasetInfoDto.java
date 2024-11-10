package com.backend.thesis.domain.dto;

public class DatasetInfoDto {
    private Long idDataset;
    private String datasetName;
    private String frequencyType;

    public Long getIdDataset() {
        return this.idDataset;
    }

    public String getDatasetName() {
        return this.datasetName;
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

    public void setFrequencyType(final String frequencyType) {
        this.frequencyType = frequencyType;
    }
}
