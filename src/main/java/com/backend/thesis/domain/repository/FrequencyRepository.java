package com.backend.thesis.domain.repository;

import com.backend.thesis.domain.entity.FrequencyEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface FrequencyRepository extends CrudRepository<FrequencyEntity, Long> {
    @Query(nativeQuery = true, value = "SELECT * FROM frequency a WHERE a.frequency_type = ?1 LIMIT 1")
    FrequencyEntity findByFrequencyType(final String frequency);
}
