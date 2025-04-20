package com.samee.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobApplicationDto {
    private Long id;
    private Long jobId;
    private String username;
    private LocalDateTime appliedDate;
    private String status;
    private String coverLetter;
    private MultipartFile resume;
    private String resumeUrl;
}