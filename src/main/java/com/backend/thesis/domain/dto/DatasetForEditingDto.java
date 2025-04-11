package com.backend.thesis.domain.dto;

import com.backend.thesis.utility.Type;

import java.util.List;

public class DatasetForEditingDto {
    private DatasetInfoDto datasetInfoDto;
    private List<Type.DatasetRow> rows;

    public double[] internalGetRawValues() {
        final double[] values = new double[this.rows.size()];

        for (int rowIndex = 0; rowIndex < this.rows.size(); ++rowIndex) {
            final Type.DatasetRow row = this.rows.get(rowIndex);

            if (row.value().isEmpty()) {
                throw new RuntimeException("Dataset obsahuje chýbajúce hodnoty");
            }

            final double value = Double.parseDouble(row.value());
            values[rowIndex] = value;
        }

        return values;
    }

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
