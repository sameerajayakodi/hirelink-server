package com.samee.server.service.impl;

import com.samee.server.entity.Trainer;
import com.samee.server.repo.TrainerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class TrainerDetailsServiceImpl implements UserDetailsService {

    private final TrainerRepo trainerRepo;

    @Autowired
    public TrainerDetailsServiceImpl(TrainerRepo trainerRepo) {
        this.trainerRepo = trainerRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Trainer trainer = trainerRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Trainer not found with username: " + username));

        return new User(
                trainer.getUsername(),
                trainer.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("TRAINER"))
        );
    }
}