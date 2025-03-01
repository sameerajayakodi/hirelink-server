package com.samee.server.service.impl;

import com.samee.server.dto.TrainerDto;
import com.samee.server.entity.Trainer;
import com.samee.server.repo.TrainerRepo;
import com.samee.server.service.TrainerService;
import com.samee.server.service.auth.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TrainerServiceImpl implements TrainerService {

    private final TrainerRepo trainerRepo;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;

    @Autowired
    public TrainerServiceImpl(TrainerRepo trainerRepo, PasswordEncoder passwordEncoder, JWTService jwtService) {
        this.trainerRepo = trainerRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public void registerTrainer(TrainerDto trainerDto) {
        // Check if trainer already exists
        if (trainerRepo.existsByUsername(trainerDto.getUsername())) {
            throw new RuntimeException("Trainer with username " + trainerDto.getUsername() + " already exists");
        }

        if (trainerRepo.existsByEmail(trainerDto.getEmail())) {
            throw new RuntimeException("Trainer with email " + trainerDto.getEmail() + " already exists");
        }

        // Create new trainer entity
        Trainer trainer = new Trainer();
        trainer.setUsername(trainerDto.getUsername());
        trainer.setEmail(trainerDto.getEmail());
        trainer.setPassword(passwordEncoder.encode(trainerDto.getPassword()));
        trainer.setFullName(trainerDto.getFullName());
        trainer.setSpecialization(trainerDto.getSpecialization());
        trainer.setBio(trainerDto.getBio());
        trainer.setProfilePicture(trainerDto.getProfilePicture());
        trainer.setExperience(trainerDto.getExperience());

        // Save the trainer
        trainerRepo.save(trainer);
    }

    @Override
    public String login(TrainerDto trainerDto) {
        // Find trainer by username
        Optional<Trainer> optionalTrainer = trainerRepo.findByUsername(trainerDto.getUsername());

        if (optionalTrainer.isEmpty()) {
            throw new RuntimeException("Invalid username or password");
        }

        Trainer trainer = optionalTrainer.get();

        // Verify password
        if (!passwordEncoder.matches(trainerDto.getPassword(), trainer.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        // Generate JWT token
        return jwtService.generateToken(trainer.getUsername(), "TRAINER");
    }

    @Override
    public List<TrainerDto> getAllTrainers() {
        return trainerRepo.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public TrainerDto getTrainerByUsername(String username) {
        return trainerRepo.findByUsername(username)
                .map(this::convertToDto)
                .orElseThrow(() -> new RuntimeException("Trainer not found with username: " + username));
    }

    @Override
    public TrainerDto updateTrainer(String username, TrainerDto trainerDto) {
        Trainer trainer = trainerRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Trainer not found with username: " + username));

        // Update fields (except password which requires special handling)
        trainer.setEmail(trainerDto.getEmail());
        trainer.setFullName(trainerDto.getFullName());
        trainer.setSpecialization(trainerDto.getSpecialization());
        trainer.setBio(trainerDto.getBio());
        trainer.setProfilePicture(trainerDto.getProfilePicture());
        trainer.setExperience(trainerDto.getExperience());

        // Handle password update if provided
        if (trainerDto.getPassword() != null && !trainerDto.getPassword().isEmpty()) {
            trainer.setPassword(passwordEncoder.encode(trainerDto.getPassword()));
        }

        // Save updated trainer
        Trainer updatedTrainer = trainerRepo.save(trainer);
        return convertToDto(updatedTrainer);
    }

    @Override
    public TrainerDto deleteTrainer(String username) {
        Trainer trainer = trainerRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Trainer not found with username: " + username));

        TrainerDto trainerDto = convertToDto(trainer);
        trainerRepo.delete(trainer);

        return trainerDto;
    }

    // Helper method to convert entity to DTO
    private TrainerDto convertToDto(Trainer trainer) {
        TrainerDto dto = new TrainerDto();
        dto.setId(trainer.getId());
        dto.setUsername(trainer.getUsername());
        dto.setEmail(trainer.getEmail());
        dto.setFullName(trainer.getFullName());
        dto.setSpecialization(trainer.getSpecialization());
        dto.setBio(trainer.getBio());
        dto.setProfilePicture(trainer.getProfilePicture());
        dto.setExperience(trainer.getExperience());
        // Don't set password for security reasons
        return dto;
    }
}