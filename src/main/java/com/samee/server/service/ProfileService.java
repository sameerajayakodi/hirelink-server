package com.samee.server.service;

import com.samee.server.dto.ProfileDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProfileService {
    /**
     * Create a new profile for a user
     */
    ProfileDto createProfile(ProfileDto profileDto, String username);

    /**
     * Get profile by username
     */
    ProfileDto getProfileByUsername(String username);

    /**
     * Update an existing profile
     */
    ProfileDto updateProfile(ProfileDto profileDto, String username);

    /**
     * Delete a profile
     */
    boolean deleteProfile(String username);

    /**
     * Upload CV document and attach to profile
     */
    ProfileDto uploadCV(MultipartFile file, String username);

    /**
     * Add skills to profile
     */
    ProfileDto addSkills(List<String> skills, String username);
}