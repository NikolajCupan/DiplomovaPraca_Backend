package com.backend.thesis.domain.repository;

import com.backend.thesis.domain.entity.DatasetEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IDatasetRepository extends CrudRepository<DatasetEntity, Long> {
    @Query(nativeQuery = true, value = "SELECT * FROM dataset a WHERE a.id_user = ?1")
    List<DatasetEntity> findByIdUser(final Long idUser);
}
