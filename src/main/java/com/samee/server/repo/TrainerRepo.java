package com.samee.server.repo;

import com.samee.server.entity.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrainerRepo extends JpaRepository<Trainer, Long> {
    Optional<Trainer> findByUsername(String username);
    Optional<Trainer> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}