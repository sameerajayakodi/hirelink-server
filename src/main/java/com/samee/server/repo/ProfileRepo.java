package com.samee.server.repo;

import com.samee.server.entity.Profile;
import com.samee.server.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepo extends JpaRepository<Profile, String> {
    Optional<Profile> findByUser(User user);
    Optional<Profile> findByEmail(String email);
    boolean existsByUser(User user);
    boolean existsByEmail(String email);
}