package com.backend.thesis.domain.dto;

import com.backend.thesis.helper.Frequency;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

public class DatasetDto {
    private Date startDate;
    private MultipartFile file;
    private Frequency frequency;
}
