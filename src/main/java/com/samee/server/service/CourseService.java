package com.samee.server.service;

import com.samee.server.dto.CourseDto;
import java.util.List;

public interface CourseService {
    CourseDto createCourse(CourseDto courseDto, String trainerUsername);
    List<CourseDto> getAllCourses();
    CourseDto getCourseById(Long id);
    List<CourseDto> getCoursesByTrainer(String trainerUsername);
    List<CourseDto> getCoursesByCategory(String category);
    List<CourseDto> searchCourses(String keyword);
    CourseDto updateCourse(Long id, CourseDto courseDto, String trainerUsername);
    void deleteCourse(Long id, String trainerUsername);
}