package com.samee.server.utils;

import com.samee.server.dto.ProfileDto;
import com.samee.server.dto.UserDto;
import com.samee.server.entity.Profile;
import com.samee.server.entity.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;


@Component
public class Converter {

    private final ModelMapper modelMapper;

    @Autowired
    public Converter(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public User userDtoToEntity(UserDto userDto) {
        return modelMapper.map(userDto, User.class);
    }

    public UserDto entityToUserDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }

    public List<UserDto> userListToDtoList(List<User> users) {
        return users.stream()
                .map(this::entityToUserDto)
                .collect(Collectors.toList());
    }

    public Profile profileDtoToEntity(ProfileDto profileDto) {
        Profile profile = new Profile();

        // Skip ID, it will be generated
        // Skip user, it will be set in service
        profile.setEmail(profileDto.getEmail());
        profile.setPhone(profileDto.getPhone());
        profile.setFullName(profileDto.getFullName());
        profile.setBio(profileDto.getBio());
        profile.setDescription(profileDto.getDescription());
        profile.setSkills(profileDto.getSkills());
        profile.setLinkedinProfile(profileDto.getLinkedinProfile());
        profile.setGithubProfile(profileDto.getGithubProfile());
        profile.setPortfolioUrl(profileDto.getPortfolioUrl());
        profile.setTwitterProfile(profileDto.getTwitterProfile());
        profile.setCvDocumentId(profileDto.getCvDocumentId());

        return profile;
    }

    /**
     * Converts Profile entity to ProfileDto
     */
    public ProfileDto entityToProfileDto(Profile profile) {
        ProfileDto profileDto = new ProfileDto();

        profileDto.setId(profile.getId());
        profileDto.setUsername(profile.getUser().getUsername());
        profileDto.setEmail(profile.getEmail());
        profileDto.setPhone(profile.getPhone());
        profileDto.setFullName(profile.getFullName());
        profileDto.setBio(profile.getBio());
        profileDto.setDescription(profile.getDescription());
        profileDto.setSkills(profile.getSkills());
        profileDto.setLinkedinProfile(profile.getLinkedinProfile());
        profileDto.setGithubProfile(profile.getGithubProfile());
        profileDto.setPortfolioUrl(profile.getPortfolioUrl());
        profileDto.setTwitterProfile(profile.getTwitterProfile());
        profileDto.setCvDocumentId(profile.getCvDocumentId());
        profileDto.setCreatedAt(profile.getCreatedAt());
        profileDto.setUpdatedAt(profile.getUpdatedAt());

        return profileDto;
    }

}
