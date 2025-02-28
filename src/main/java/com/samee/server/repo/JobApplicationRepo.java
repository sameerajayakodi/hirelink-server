package com.samee.server.repo;


import com.samee.server.entity.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobApplicationRepo extends JpaRepository<JobApplication, Long> {
    List<JobApplication> findByUserId(String userId);
    List<JobApplication> findByJobId(Long jobId);
    Optional<JobApplication> findByJobIdAndUserId(Long jobId, String userId);
}

