package com.samee.server.controller;

import com.samee.server.dto.JobDto;
import com.samee.server.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1/jobs")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
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

    @GetMapping("/{id}")
    public ResponseEntity<JobDto> getJobById(@PathVariable Long id) {
        JobDto job = jobService.getJobById(id);
        return ResponseEntity.ok(job);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('COMPANY')")
    public ResponseEntity<?> updateJob(@PathVariable Long id, @RequestBody JobDto jobDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String companyName = auth.getName();

        try {
            JobDto updatedJob = jobService.updateJob(id, jobDto, companyName);
            return ResponseEntity.ok(updatedJob);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('COMPANY')")
    public ResponseEntity<?> updateJobStatus(@PathVariable Long id, @RequestBody Map<String, Boolean> statusUpdate) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String companyName = auth.getName();

        try {
            Boolean isActive = statusUpdate.get("isActive");
            if (isActive == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "isActive field is required"));
            }

            // Get the existing job
            JobDto job = jobService.getJobById(id);

            // Update only the status
            job.setIsActive(isActive);

            // Save the updated job
            JobDto updatedJob = jobService.updateJob(id, job, companyName);
            return ResponseEntity.ok(Map.of(
                    "message", "Job status updated successfully",
                    "job", updatedJob
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/company/{companyName}")
    public ResponseEntity<List<JobDto>> getJobsByCompanyName(@PathVariable String companyName) {
        List<JobDto> jobs = jobService.getJobsByCompany(companyName);
        return ResponseEntity.ok(jobs);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('COMPANY')")
    public ResponseEntity<?> deleteJob(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String companyName = auth.getName();

        try {
            boolean result = jobService.deleteJob(id, companyName);
            if (result) {
                return ResponseEntity.ok(Map.of("message", "Job deleted successfully"));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "Failed to delete job"));
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}