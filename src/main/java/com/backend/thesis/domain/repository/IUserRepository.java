package com.backend.thesis.domain.repository;

import com.backend.thesis.domain.entity.UserEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserRepository extends CrudRepository<UserEntity, Long> {
    @Query(nativeQuery = true, value = "SELECT * FROM user a WHERE a.cookie = ?1 LIMIT 1")
    UserEntity findByCookie(final String cookie);
}
