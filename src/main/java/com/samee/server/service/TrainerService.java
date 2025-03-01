package com.samee.server.service;

import com.samee.server.dto.TrainerDto;
import java.util.List;

public interface TrainerService {
    void registerTrainer(TrainerDto trainerDto);
    String login(TrainerDto trainerDto);
    List<TrainerDto> getAllTrainers();
    TrainerDto getTrainerByUsername(String username);
    TrainerDto updateTrainer(String username, TrainerDto trainerDto);
    TrainerDto deleteTrainer(String username);
}