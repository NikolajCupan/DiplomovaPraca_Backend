package com.backend.thesis.domain.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "dataset")
public class DatasetEntity {
    @JoinColumn(name = "id_user", referencedColumnName = "id_user")
    @Column(name = "id_user", nullable = false)
    private Long idUser;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_dataset", nullable = false, unique = true)
    private Long idDataset;

    @Column(name = "md5", nullable = false, unique = true)
    private String md5;

    @Column(name = "file_name", nullable = false, unique = true)
    private String fileName;
}
