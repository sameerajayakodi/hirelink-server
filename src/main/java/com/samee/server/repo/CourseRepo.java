package com.samee.server.repo;

import com.samee.server.entity.Course;
import com.samee.server.entity.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepo extends JpaRepository<Course, Long> {
    List<Course> findByTrainer(Trainer trainer);
    List<Course> findByCategory(String category);
    List<Course> findByTitleContainingIgnoreCase(String keyword);
}