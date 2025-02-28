package com.samee.server.service.impl;


import com.samee.server.dto.JobApplicationDto;
import com.samee.server.entity.Job;
import com.samee.server.entity.JobApplication;
import com.samee.server.entity.User;
import com.samee.server.repo.JobApplicationRepo;
import com.samee.server.repo.JobRepo;
import com.samee.server.repo.UserRepo;
import com.samee.server.service.FileStorageService;
import com.samee.server.service.JobApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobApplicationServiceImpl implements JobApplicationService {
    private final JobApplicationRepo applicationRepository;
    private final JobRepo jobRepository;
    private final UserRepo userRepo;
    private final FileStorageService fileStorageService;

    @Autowired
    public JobApplicationServiceImpl(JobApplicationRepo applicationRepository,
                                     JobRepo jobRepository,
                                     UserRepo userRepo,
                                     FileStorageService fileStorageService) {
        this.applicationRepository = applicationRepository;
        this.jobRepository = jobRepository;
        this.userRepo = userRepo;
        this.fileStorageService = fileStorageService;
    }

    @Override
    public JobApplicationDto applyForJob(JobApplicationDto applicationDto, String username) {
        User user = userRepo.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        Job job = jobRepository.findById(applicationDto.getJobId())
                .orElseThrow(() -> new RuntimeException("Job not found"));

        // Check if user already applied
        if (applicationRepository.findByJobIdAndUserId(job.getId(), user.getId()).isPresent()) {
            throw new RuntimeException("You have already applied for this job");
        }

        // Upload resume
        String resumeUrl = null;
        if (applicationDto.getResume() != null) {
            resumeUrl = fileStorageService.storeFile(applicationDto.getResume());
        }

        JobApplication application = new JobApplication();
        application.setJob(job);
        application.setUser(user);
        application.setAppliedDate(LocalDateTime.now());
        application.setStatus(ApplicationStatus.PENDING);
        application.setCoverLetter(applicationDto.getCoverLetter());
        application.setResumeUrl(resumeUrl);

        JobApplication savedApplication = applicationRepository.save(application);
        return convertToDto(savedApplication);
    }

    @Override
    public List<JobApplicationDto> getApplicationsByUser(String username) {
        User user = userRepo.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        List<JobApplication> applications = applicationRepository.findByUserId(user.getId());
        return applications.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<JobApplicationDto> getApplicationsByJob(Long jobId) {
        jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        List<JobApplication> applications = applicationRepository.findByJobId(jobId);
        return applications.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public JobApplicationDto getApplicationById(Long applicationId) {
        JobApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        return convertToDto(application);
    }

    @Override
    public JobApplicationDto updateApplicationStatus(Long applicationId, String status, String companyName) {
        JobApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        // Verify company ownership
        if (!application.getJob().getCompany().getName().equals(companyName)) {
            throw new RuntimeException("You are not authorized to update this application");
        }

        try {
            ApplicationStatus newStatus = ApplicationStatus.valueOf(status.toUpperCase());
            application.setStatus(newStatus);
            application.setLastUpdated(LocalDateTime.now());

            JobApplication updatedApplication = applicationRepository.save(application);
            return convertToDto(updatedApplication);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid application status: " + status);
        }
    }

    private JobApplicationDto convertToDto(JobApplication application) {
        JobApplicationDto dto = new JobApplicationDto();
        dto.setId(application.getId());
        dto.setJobId(application.getJob().getId());
        dto.setUsername(application.getUser().getUsername());
        dto.setAppliedDate(application.getAppliedDate());
        dto.setStatus(application.getStatus().name());
        dto.setCoverLetter(application.getCoverLetter());
        dto.setResumeUrl(application.getResumeUrl());
        return dto;
    }
}
