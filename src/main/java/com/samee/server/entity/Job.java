package com.samee.server.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "jobs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    private String location;

    private String jobType; // Full-time, Part-time, Contract, etc.

    private Double salary;

    private String requirements;

    private String responsibilities;

    @Column(nullable = false)
    private LocalDateTime postedDate;

    private LocalDateTime expiryDate;

    private Boolean isActive = true;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;
}
