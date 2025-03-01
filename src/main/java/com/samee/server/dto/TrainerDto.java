package com.samee.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainerDto {
    private Long id;
    private String username;
    private String email;
    private String password;
    private String fullName;
    private String specialization;
    private String bio;
    private String profilePicture;
    private String experience;
}