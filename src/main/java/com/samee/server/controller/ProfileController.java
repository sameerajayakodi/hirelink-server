package com.samee.server.controller;

import com.samee.server.dto.ProfileDto;
import com.samee.server.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/v1/profile")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class ProfileController {

    private final ProfileService profileService;

    @Autowired
    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @PostMapping("/create")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createProfile(@RequestBody ProfileDto profileDto, Authentication authentication) {
        try {
            String username = authentication.getName();
            ProfileDto createdProfile = profileService.createProfile(profileDto, username);
            return new ResponseEntity<>(createdProfile, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getMyProfile(Authentication authentication) {
        try {
            String username = authentication.getName();
            ProfileDto profile = profileService.getProfileByUsername(username);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<?> getProfileByUsername(@PathVariable String username) {
        try {
            ProfileDto profile = profileService.getProfileByUsername(username);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/update")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateProfile(@RequestBody ProfileDto profileDto, Authentication authentication) {
        try {
            String username = authentication.getName();
            ProfileDto updatedProfile = profileService.updateProfile(profileDto, username);
            return ResponseEntity.ok(updatedProfile);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteProfile(Authentication authentication) {
        try {
            String username = authentication.getName();
            boolean deleted = profileService.deleteProfile(username);
            if (deleted) {
                return ResponseEntity.ok("Profile deleted successfully");
            } else {
                return new ResponseEntity<>("Failed to delete profile", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/upload-cv")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> uploadCV(@RequestParam("file") MultipartFile file, Authentication authentication) {
        try {
            String username = authentication.getName();
            ProfileDto updatedProfile = profileService.uploadCV(file, username);
            return ResponseEntity.ok(updatedProfile);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/add-skills")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> addSkills(@RequestBody List<String> skills, Authentication authentication) {
        try {
            String username = authentication.getName();
            ProfileDto updatedProfile = profileService.addSkills(skills, username);
            return ResponseEntity.ok(updatedProfile);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
