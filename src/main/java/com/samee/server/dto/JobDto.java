package com.samee.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobDto {
    private Long id;
    private String title;
    private String description;
    private String location;
    private String jobType;
    private Double salary;
    private String requirements;
    private String responsibilities;
    private LocalDateTime postedDate;
    private LocalDateTime expiryDate;
    private Boolean isActive;
    private String companyName;
}
