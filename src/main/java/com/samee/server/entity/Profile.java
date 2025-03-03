package com.samee.server.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "profile_id")
    private String id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(unique = true)
    private String email;

    private String phone;

    private String fullName;

    @Column(length = 1000)
    private String bio;

    @Column(length = 1500)
    private String description;

    @ElementCollection
    @CollectionTable(name = "profile_skills", joinColumns = @JoinColumn(name = "profile_id"))
    @Column(name = "skill")
    private List<String> skills = new ArrayList<>();

    private String linkedinProfile;

    private String githubProfile;

    private String portfolioUrl;

    private String twitterProfile;

    // Document link for CV - can be used to reference a document from Document entity
    private String cvDocumentId;

    // Timestamp fields
    @Column(updatable = false)
    private Long createdAt;

    private Long updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = System.currentTimeMillis();
        updatedAt = System.currentTimeMillis();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = System.currentTimeMillis();
    }
}