package com.samee.server.repo;


import com.samee.server.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepo extends JpaRepository<Job, Long> {
    // Find jobs by company name (assuming company name is a field in Job entity via the Company relationship)
    List<Job> findByCompanyName(String companyName);

    // Find jobs that are active
    List<Job> findByIsActiveTrue();

    // Find jobs by company ID (useful if company ID is used for reference)
    @Query("SELECT j FROM Job j WHERE j.company.id = :companyId")
    List<Job> findByCompanyId(@Param("companyId") Long companyId);

    // Find active jobs for a specific company
    List<Job> findByCompanyNameAndIsActiveTrue(String companyName);

    // Find jobs by location (partial match)
    @Query("SELECT j FROM Job j WHERE LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%'))")
    List<Job> findByLocationContaining(@Param("location") String location);

    // Find jobs by title (partial match)
    @Query("SELECT j FROM Job j WHERE LOWER(j.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    List<Job> findByTitleContaining(@Param("title") String title);
}
