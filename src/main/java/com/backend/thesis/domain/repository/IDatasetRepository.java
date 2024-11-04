package com.backend.thesis.domain.repository;

import com.backend.thesis.domain.entity.DatasetEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IDatasetRepository extends CrudRepository<DatasetEntity, Long> {
}
