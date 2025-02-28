package com.samee.server.repo;


import com.samee.server.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepo extends JpaRepository<User, Integer> {
    boolean existsByUsername(String username);
    User findByUsername(String username);
}
