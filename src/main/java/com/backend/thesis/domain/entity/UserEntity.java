package com.backend.thesis.domain.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user", nullable = false, unique = true)
    private Long idUser;

    @Column(name = "cookie", nullable = false, unique = true)
    private String cookie;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "last_accessed_at", nullable = false)
    private LocalDateTime lastAccessedAt;

    public UserEntity() {
    }

    public UserEntity(final String cookie, final LocalDateTime createdAt, final LocalDateTime lastAccessedAt) {
        this.cookie = cookie;
        this.createdAt = createdAt;
        this.lastAccessedAt = lastAccessedAt;
    }

    public Long getIdUser() {
        return this.idUser;
    }

    public String getCookie() {
        return this.cookie;
    }

    public void setLastAccessedAt(final LocalDateTime lastAccessedAt) {
        this.lastAccessedAt = lastAccessedAt;
    }
}
