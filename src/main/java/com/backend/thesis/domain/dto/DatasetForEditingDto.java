package com.backend.thesis.domain.dto;

import com.backend.thesis.utility.Type;

import java.util.List;

public class DatasetForEditingDto {
    private DatasetInfoDto datasetInfoDto;
    private List<Type.DatasetRow> rows;

    public DatasetInfoDto getDatasetInfoDto() {
        return this.datasetInfoDto;
    }

    public List<Type.DatasetRow> getRows() {
        return this.rows;
    }

    public void setDatasetInfoDto(final DatasetInfoDto datasetInfoDto) {
        this.datasetInfoDto = datasetInfoDto;
    }

    public void setRows(final List<Type.DatasetRow> rows) {
        this.rows = rows;
    }
}
