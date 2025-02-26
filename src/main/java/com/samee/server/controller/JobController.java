package com.samee.server.controller;

import net.nighthawk.seekersconnect_backend.dto.JobDto;
import net.nighthawk.seekersconnect_backend.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/jobs")
public class JobController {
    private final JobService jobService;

    @Autowired
    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('COMPANY')")
    public ResponseEntity<JobDto> createJob(@RequestBody JobDto jobDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String companyName = auth.getName();

        JobDto createdJob = jobService.createJob(jobDto, companyName);
        return new ResponseEntity<>(createdJob, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<JobDto>> getAllActiveJobs() {
        List<JobDto> jobs = jobService.getAllActiveJobs();
        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/company")
    @PreAuthorize("hasAuthority('COMPANY')")
    public ResponseEntity<List<JobDto>> getCompanyJobs() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String companyName = auth.getName();

        List<JobDto> jobs = jobService.getJobsByCompany(companyName);
        return ResponseEntity.ok(jobs);
    }

    // Add more endpoints for updating, deleting jobs
}