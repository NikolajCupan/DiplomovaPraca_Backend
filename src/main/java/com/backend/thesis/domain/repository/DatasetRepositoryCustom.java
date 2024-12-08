package com.backend.thesis.domain.repository;

import com.backend.thesis.utility.other.RequestException;

public interface DatasetRepositoryCustom {
    void deleteByIdDataset(final Long idDataset) throws RequestException;
}
