package com.samee.server.service.impl;


import com.samee.server.dto.JobDto;
import com.samee.server.entity.Company;
import com.samee.server.entity.Job;
import com.samee.server.repo.CompanyRepo;
import com.samee.server.repo.JobRepo;
import com.samee.server.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobServiceImpl implements JobService {
    private final JobRepo jobRepository;
    private final CompanyRepo companyRepo;

    @Autowired
    public JobServiceImpl(JobRepo jobRepository, CompanyRepo companyRepo) {
        this.jobRepository = jobRepository;
        this.companyRepo = companyRepo;
    }

    @Override
    public JobDto createJob(JobDto jobDto, String companyName) {
        Company company = companyRepo.findByName(companyName)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        Job job = new Job();
        job.setTitle(jobDto.getTitle());
        job.setDescription(jobDto.getDescription());
        job.setLocation(jobDto.getLocation());
        job.setJobType(jobDto.getJobType());
        job.setSalary(jobDto.getSalary());
        job.setRequirements(jobDto.getRequirements());
        job.setResponsibilities(jobDto.getResponsibilities());
        job.setPostedDate(LocalDateTime.now());
        job.setExpiryDate(jobDto.getExpiryDate());
        job.setIsActive(true);
        job.setCompany(company);

        Job savedJob = jobRepository.save(job);
        return convertToDto(savedJob);
    }

    @Override
    public List<JobDto> getAllActiveJobs() {
        List<Job> activeJobs = jobRepository.findByIsActiveTrue();
        return activeJobs.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<JobDto> getJobsByCompany(String companyName) {
        List<Job> companyJobs = jobRepository.findByCompanyName(companyName);
        return companyJobs.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public JobDto getJobById(Long jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));
        return convertToDto(job);
    }

    @Override
    public JobDto updateJob(Long jobId, JobDto jobDto, String companyName) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        // Verify ownership
        if (!job.getCompany().getName().equals(companyName)) {
            throw new RuntimeException("You are not authorized to update this job");
        }

        // Update job fields
        job.setTitle(jobDto.getTitle());
        job.setDescription(jobDto.getDescription());
        job.setLocation(jobDto.getLocation());
        job.setJobType(jobDto.getJobType());
        job.setSalary(jobDto.getSalary());
        job.setRequirements(jobDto.getRequirements());
        job.setResponsibilities(jobDto.getResponsibilities());
        job.setExpiryDate(jobDto.getExpiryDate());
        job.setIsActive(jobDto.getIsActive());

        Job updatedJob = jobRepository.save(job);
        return convertToDto(updatedJob);
    }

    @Override
    public boolean deleteJob(Long jobId, String companyName) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        // Verify ownership
        if (!job.getCompany().getName().equals(companyName)) {
            throw new RuntimeException("You are not authorized to delete this job");
        }

        // Soft delete - mark as inactive
        job.setIsActive(false);
        jobRepository.save(job);

        return true;
    }

    private JobDto convertToDto(Job job) {
        JobDto dto = new JobDto();
        dto.setId(job.getId());
        dto.setTitle(job.getTitle());
        dto.setDescription(job.getDescription());
        dto.setLocation(job.getLocation());
        dto.setJobType(job.getJobType());
        dto.setSalary(job.getSalary());
        dto.setRequirements(job.getRequirements());
        dto.setResponsibilities(job.getResponsibilities());
        dto.setPostedDate(job.getPostedDate());
        dto.setExpiryDate(job.getExpiryDate());
        dto.setIsActive(job.getIsActive());
        dto.setCompanyName(job.getCompany().getName());
        return dto;
    }
}