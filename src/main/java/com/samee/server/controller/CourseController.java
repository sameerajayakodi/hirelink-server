package com.samee.server.controller;

import com.samee.server.dto.CourseDto;
import com.samee.server.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("api/v1/courses")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class CourseController {

    private final CourseService courseService;

    @Autowired
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('TRAINER')")
    public ResponseEntity<?> createCourse(@RequestBody CourseDto courseDto) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String trainerUsername = authentication.getName();

            CourseDto createdCourse = courseService.createCourse(courseDto, trainerUsername);
            return new ResponseEntity<>(createdCourse, HttpStatus.CREATED);
        } catch (Exception exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<CourseDto>> getAllCourses() {
        try {
            List<CourseDto> courses = courseService.getAllCourses();

            if (courses.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }

            return ResponseEntity.ok(courses);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.emptyList());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseDto> getCourseById(@PathVariable Long id) {
        try {
            CourseDto course = courseService.getCourseById(id);
            return ResponseEntity.ok(course);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/trainer")
    @PreAuthorize("hasAuthority('TRAINER')")
    public ResponseEntity<List<CourseDto>> getTrainerCourses() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String trainerUsername = authentication.getName();

            List<CourseDto> courses = courseService.getCoursesByTrainer(trainerUsername);

            if (courses.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }

            return ResponseEntity.ok(courses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.emptyList());
        }
    }

    @GetMapping("/trainer/{username}")
    public ResponseEntity<List<CourseDto>> getCoursesByTrainer(@PathVariable String username) {
        try {
            List<CourseDto> courses = courseService.getCoursesByTrainer(username);

            if (courses.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }

            return ResponseEntity.ok(courses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.emptyList());
        }
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<CourseDto>> getCoursesByCategory(@PathVariable String category) {
        try {
            List<CourseDto> courses = courseService.getCoursesByCategory(category);

            if (courses.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }

            return ResponseEntity.ok(courses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.emptyList());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<CourseDto>> searchCourses(@RequestParam String keyword) {
        try {
            List<CourseDto> courses = courseService.searchCourses(keyword);

            if (courses.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }

            return ResponseEntity.ok(courses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.emptyList());
        }
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAuthority('TRAINER')")
    public ResponseEntity<?> updateCourse(@PathVariable Long id, @RequestBody CourseDto courseDto) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String trainerUsername = authentication.getName();

            CourseDto updatedCourse = courseService.updateCourse(id, courseDto, trainerUsername);
            return ResponseEntity.ok(updatedCourse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('TRAINER')")
    public ResponseEntity<String> deleteCourse(@PathVariable Long id) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String trainerUsername = authentication.getName();

            courseService.deleteCourse(id, trainerUsername);
            return ResponseEntity.ok("Course successfully deleted");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}