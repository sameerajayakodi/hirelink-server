package com.samee.server.service.impl;

import com.samee.server.dto.ProfileDto;
import com.samee.server.entity.Document;
import com.samee.server.entity.Profile;
import com.samee.server.entity.User;
import com.samee.server.repo.ProfileRepo;
import com.samee.server.repo.UserRepo;
import com.samee.server.service.DocumentService;
import com.samee.server.service.ProfileService;
import com.samee.server.utils.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepo profileRepo;
    private final UserRepo userRepo;
    private final Converter converter;
    private final DocumentService documentService;

    @Autowired
    public ProfileServiceImpl(ProfileRepo profileRepo, UserRepo userRepo, Converter converter, DocumentService documentService) {
        this.profileRepo = profileRepo;
        this.userRepo = userRepo;
        this.converter = converter;
        this.documentService = documentService;
    }

    @Override
    @Transactional
    public ProfileDto createProfile(ProfileDto profileDto, String username) {
        User user = userRepo.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        // Check if profile already exists
        if (profileRepo.existsByUser(user)) {
            throw new RuntimeException("Profile already exists for user: " + username);
        }

        // Check if email is already in use
        if (profileDto.getEmail() != null && profileRepo.existsByEmail(profileDto.getEmail())) {
            throw new RuntimeException("Email already in use: " + profileDto.getEmail());
        }

        Profile profile = converter.profileDtoToEntity(profileDto);
        profile.setUser(user);

        Profile savedProfile = profileRepo.save(profile);
        return converter.entityToProfileDto(savedProfile);
    }

    @Override
    public ProfileDto getProfileByUsername(String username) {
        User user = userRepo.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        Optional<Profile> profileOpt = profileRepo.findByUser(user);
        if (profileOpt.isEmpty()) {
            throw new RuntimeException("Profile not found for user: " + username);
        }

        return converter.entityToProfileDto(profileOpt.get());
    }

    @Override
    @Transactional
    public ProfileDto updateProfile(ProfileDto profileDto, String username) {
        User user = userRepo.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        Optional<Profile> profileOpt = profileRepo.findByUser(user);
        if (profileOpt.isEmpty()) {
            throw new RuntimeException("Profile not found for user: " + username);
        }

        Profile existingProfile = profileOpt.get();

        // Email uniqueness check if changing the email
        if (profileDto.getEmail() != null && !profileDto.getEmail().equals(existingProfile.getEmail())) {
            if (profileRepo.existsByEmail(profileDto.getEmail())) {
                throw new RuntimeException("Email already in use: " + profileDto.getEmail());
            }
            existingProfile.setEmail(profileDto.getEmail());
        }

        // Update fields if provided in the DTO
        if (profileDto.getPhone() != null) existingProfile.setPhone(profileDto.getPhone());
        if (profileDto.getFullName() != null) existingProfile.setFullName(profileDto.getFullName());
        if (profileDto.getBio() != null) existingProfile.setBio(profileDto.getBio());
        if (profileDto.getDescription() != null) existingProfile.setDescription(profileDto.getDescription());
        if (profileDto.getLinkedinProfile() != null) existingProfile.setLinkedinProfile(profileDto.getLinkedinProfile());
        if (profileDto.getGithubProfile() != null) existingProfile.setGithubProfile(profileDto.getGithubProfile());
        if (profileDto.getPortfolioUrl() != null) existingProfile.setPortfolioUrl(profileDto.getPortfolioUrl());
        if (profileDto.getTwitterProfile() != null) existingProfile.setTwitterProfile(profileDto.getTwitterProfile());

        // Keep existing skills if not provided
        if (profileDto.getSkills() != null && !profileDto.getSkills().isEmpty()) {
            existingProfile.setSkills(profileDto.getSkills());
        }

        Profile updatedProfile = profileRepo.save(existingProfile);
        return converter.entityToProfileDto(updatedProfile);
    }

    @Override
    @Transactional
    public boolean deleteProfile(String username) {
        User user = userRepo.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        Optional<Profile> profileOpt = profileRepo.findByUser(user);
        if (profileOpt.isEmpty()) {
            throw new RuntimeException("Profile not found for user: " + username);
        }

        Profile profile = profileOpt.get();

        // Delete CV document if exists
        if (profile.getCvDocumentId() != null) {
            // You need to implement a way to delete the document
            // This assumes documentService has a method to delete by ID
            documentService.deleteDocument(profile.getCvDocumentId());
        }

        profileRepo.delete(profile);
        return true;
    }

    @Override
    @Transactional
    public ProfileDto uploadCV(MultipartFile file, String username) {
        User user = userRepo.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        Optional<Profile> profileOpt = profileRepo.findByUser(user);
        if (profileOpt.isEmpty()) {
            throw new RuntimeException("Profile not found for user: " + username);
        }

        Profile profile = profileOpt.get();

        try {
            // Upload document using document service
            Document document = documentService.uploadDocument(file, username, "CV");

            // Set the document ID in profile
            profile.setCvDocumentId(document.getId());
            profileRepo.save(profile);

            return converter.entityToProfileDto(profile);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload CV: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public ProfileDto addSkills(List<String> skills, String username) {
        User user = userRepo.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        Optional<Profile> profileOpt = profileRepo.findByUser(user);
        if (profileOpt.isEmpty()) {
            throw new RuntimeException("Profile not found for user: " + username);
        }

        Profile profile = profileOpt.get();

        // Add new skills
        List<String> existingSkills = profile.getSkills();
        for (String skill : skills) {
            if (!existingSkills.contains(skill)) {
                existingSkills.add(skill);
            }
        }

        profile.setSkills(existingSkills);
        profileRepo.save(profile);

        return converter.entityToProfileDto(profile);
    }
}