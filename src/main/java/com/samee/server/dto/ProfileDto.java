package com.samee.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDto {
    private String id;
    private String username; // Username of the associated user
    private String email;
    private String phone;
    private String fullName;
    private String bio;
    private String description;
    private List<String> skills;
    private String linkedinProfile;
    private String githubProfile;
    private String portfolioUrl;
    private String twitterProfile;
    private String cvDocumentId;
    private Long createdAt;
    private Long updatedAt;
}
