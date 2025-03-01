package com.samee.server.controller;

import com.samee.server.dto.TrainerDto;
import com.samee.server.service.TrainerService;
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
@RequestMapping("api/v1/trainer")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class TrainerController {

    private final TrainerService trainerService;

    @Autowired
    public TrainerController(TrainerService trainerService) {
        this.trainerService = trainerService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody TrainerDto trainerDto) {
        try {
            trainerService.registerTrainer(trainerDto);
            return new ResponseEntity<>(trainerDto.getUsername() + " registered successfully as a trainer!", HttpStatus.CREATED);
        } catch (Exception exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody TrainerDto trainerDto) {
        try {
            return new ResponseEntity<>(trainerService.login(trainerDto), HttpStatus.OK);
        } catch (Exception exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/get-all")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'COMPANY')")
    public ResponseEntity<List<TrainerDto>> getAllTrainers() {
        try {
            List<TrainerDto> trainers = trainerService.getAllTrainers();

            if (trainers.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }

            return ResponseEntity.ok(trainers);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.emptyList());
        }
    }

    @GetMapping("/{username}")
    public ResponseEntity<TrainerDto> getTrainerByUsername(@PathVariable String username) {
        try {
            TrainerDto trainer = trainerService.getTrainerByUsername(username);
            return ResponseEntity.ok(trainer);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('TRAINER')")
    public ResponseEntity<?> updateTrainer(@RequestBody TrainerDto trainerDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        try {
            TrainerDto updatedTrainer = trainerService.updateTrainer(currentUsername, trainerDto);
            return ResponseEntity.ok(updatedTrainer);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{username}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> deleteTrainer(@PathVariable String username) {
        try {
            TrainerDto deletedTrainer = trainerService.deleteTrainer(username);
            return ResponseEntity.ok(deletedTrainer.getUsername() + " successfully deleted");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Trainer not found: " + e.getMessage());
        }
    }
}