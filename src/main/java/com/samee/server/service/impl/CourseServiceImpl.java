package com.samee.server.service.impl;

import com.samee.server.dto.CourseDto;
import com.samee.server.entity.Course;
import com.samee.server.entity.Trainer;
import com.samee.server.repo.CourseRepo;
import com.samee.server.repo.TrainerRepo;
import com.samee.server.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseServiceImpl implements CourseService {

    private final CourseRepo courseRepo;
    private final TrainerRepo trainerRepo;

    @Autowired
    public CourseServiceImpl(CourseRepo courseRepo, TrainerRepo trainerRepo) {
        this.courseRepo = courseRepo;
        this.trainerRepo = trainerRepo;
    }

    @Override
    public CourseDto createCourse(CourseDto courseDto, String trainerUsername) {
        Trainer trainer = trainerRepo.findByUsername(trainerUsername)
                .orElseThrow(() -> new RuntimeException("Trainer not found with username: " + trainerUsername));

        Course course = new Course();
        course.setTitle(courseDto.getTitle());
        course.setDescription(courseDto.getDescription());
        course.setCategory(courseDto.getCategory());
        course.setPrice(courseDto.getPrice());
        course.setDuration(courseDto.getDuration());
        course.setLevel(courseDto.getLevel());
        course.setTrainer(trainer);

        Course savedCourse = courseRepo.save(course);
        return convertToDto(savedCourse);
    }

    @Override
    public List<CourseDto> getAllCourses() {
        return courseRepo.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public CourseDto getCourseById(Long id) {
        Course course = courseRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + id));
        return convertToDto(course);
    }

    @Override
    public List<CourseDto> getCoursesByTrainer(String trainerUsername) {
        Trainer trainer = trainerRepo.findByUsername(trainerUsername)
                .orElseThrow(() -> new RuntimeException("Trainer not found with username: " + trainerUsername));

        return courseRepo.findByTrainer(trainer).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CourseDto> getCoursesByCategory(String category) {
        return courseRepo.findByCategory(category).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CourseDto> searchCourses(String keyword) {
        return courseRepo.findByTitleContainingIgnoreCase(keyword).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public CourseDto updateCourse(Long id, CourseDto courseDto, String trainerUsername) {
        Course course = courseRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + id));

        // Ensure that only the trainer who created the course can update it
        if (!course.getTrainer().getUsername().equals(trainerUsername)) {
            throw new RuntimeException("You are not authorized to update this course");
        }

        // Update course fields
        course.setTitle(courseDto.getTitle());
        course.setDescription(courseDto.getDescription());
        course.setCategory(courseDto.getCategory());
        course.setPrice(courseDto.getPrice());
        course.setDuration(courseDto.getDuration());
        course.setLevel(courseDto.getLevel());

        Course updatedCourse = courseRepo.save(course);
        return convertToDto(updatedCourse);
    }

    @Override
    public void deleteCourse(Long id, String trainerUsername) {
        Course course = courseRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + id));

        // Ensure that only the trainer who created the course can delete it
        if (!course.getTrainer().getUsername().equals(trainerUsername)) {
            throw new RuntimeException("You are not authorized to delete this course");
        }

        courseRepo.delete(course);
    }

    // Helper method to convert entity to DTO
    private CourseDto convertToDto(Course course) {
        CourseDto dto = new CourseDto();
        dto.setId(course.getId());
        dto.setTitle(course.getTitle());
        dto.setDescription(course.getDescription());
        dto.setCategory(course.getCategory());
        dto.setPrice(course.getPrice());
        dto.setDuration(course.getDuration());
        dto.setLevel(course.getLevel());
        dto.setCreatedAt(course.getCreatedAt());
        dto.setUpdatedAt(course.getUpdatedAt());
        dto.setTrainerId(course.getTrainer().getId());
        dto.setTrainerUsername(course.getTrainer().getUsername());
        return dto;
    }
}