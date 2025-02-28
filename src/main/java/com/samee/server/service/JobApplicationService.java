package com.samee.server.service;


import com.samee.server.dto.JobApplicationDto;

import java.util.List;

/**
 * Service for managing job applications
 */
public interface JobApplicationService {

    /**
     * Submit a job application
     *
     * @param applicationDto The application data
     * @param username The username of the applicant
     * @return The created application
     */
    JobApplicationDto applyForJob(JobApplicationDto applicationDto, String username);

    // Additional methods to be implemented as needed:

    /**
     * Get all applications submitted by a user
     *
     * @param username The username of the applicant
     * @return List of applications by the user
     */
    List<JobApplicationDto> getApplicationsByUser(String username);

    /**
     * Get all applications for a specific job
     *
     * @param jobId The ID of the job
     * @return List of applications for the job
     */
    List<JobApplicationDto> getApplicationsByJob(Long jobId);

    /**
     * Get a specific application by ID
     *
     * @param applicationId The ID of the application
     * @return The application if found
     */
    JobApplicationDto getApplicationById(Long applicationId);

    /**
     * Update the status of a job application
     *
     * @param applicationId The ID of the application
     * @param status The new status value
     * @param companyName The name of the company (for authorization)
     * @return The updated application
     */
    JobApplicationDto updateApplicationStatus(Long applicationId, String status, String companyName);
}