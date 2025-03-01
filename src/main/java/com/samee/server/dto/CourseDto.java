package com.samee.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseDto {
    private Long id;
    private String title;
    private String description;
    private String category;
    private Double price;
    private String duration;
    private String level;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long trainerId;
    private String trainerUsername; // For display purposes
}