package com.samee.server.controller;




import com.samee.server.dto.JobApplicationDto;
import com.samee.server.service.JobApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/applications")
public class JobApplicationController {
    private final JobApplicationService applicationService;

    @Autowired
    public JobApplicationController(JobApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<JobApplicationDto> applyForJob(
            @ModelAttribute JobApplicationDto applicationDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        JobApplicationDto createdApplication = applicationService.applyForJob(applicationDto, username);
        return new ResponseEntity<>(createdApplication, HttpStatus.CREATED);
    }

    @GetMapping("/user")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<List<JobApplicationDto>> getUserApplications() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        List<JobApplicationDto> applications = applicationService.getApplicationsByUser(username);
        return ResponseEntity.ok(applications);
    }

    @GetMapping("/job/{jobId}")
    @PreAuthorize("hasAuthority('COMPANY')")
    public ResponseEntity<List<JobApplicationDto>> getJobApplications(@PathVariable Long jobId) {
        List<JobApplicationDto> applications = applicationService.getApplicationsByJob(jobId);
        return ResponseEntity.ok(applications);
    }

    // Add endpoints for updating application status (for companies)
}
