package com.samee.server.service;


import com.samee.server.dto.JobDto;

import java.util.List;

/**
 * Service for managing job postings
 */
public interface JobService {

    /**
     * Create a new job posting
     *
     * @param jobDto The job data
     * @param companyName The name of the company posting the job
     * @return The created job
     */
    JobDto createJob(JobDto jobDto, String companyName);

    // Additional methods to be implemented as needed:

    /**
     * Get all active job postings
     *
     * @return List of all active jobs
     */
    List<JobDto> getAllActiveJobs();

    /**
     * Get all jobs posted by a specific company
     *
     * @param companyName The name of the company
     * @return List of jobs posted by the company
     */
    List<JobDto> getJobsByCompany(String companyName);

    /**
     * Get a job by its ID
     *
     * @param jobId The ID of the job
     * @return The job if found
     */
    JobDto getJobById(Long jobId);

    /**
     * Update an existing job
     *
     * @param jobId The ID of the job to update
     * @param jobDto The updated job data
     * @param companyName The name of the company (for authorization)
     * @return The updated job
     */
    JobDto updateJob(Long jobId, JobDto jobDto, String companyName);

    /**
     * Delete a job posting
     *
     * @param jobId The ID of the job to delete
     * @param companyName The name of the company (for authorization)
     * @return true if deletion was successful
     */
    boolean deleteJob(Long jobId, String companyName);
}
