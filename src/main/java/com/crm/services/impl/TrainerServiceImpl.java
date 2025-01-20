package com.crm.services.impl;

import com.crm.models.TrainingType;
import com.crm.repositories.TrainerRepo;
import com.crm.repositories.entities.Trainer;
import com.crm.services.TrainerService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@Transactional
public class TrainerServiceImpl extends AbstractUserService<Trainer, TrainerRepo> implements TrainerService {
    public TrainerServiceImpl(TrainerRepo repository) {
        super(repository);
    }

    @Override
    public Trainer save(String firstName, String lastName, String password, TrainingType specialization) {
        log.info("Starting saving trainer using first and last names... ");

        var newTrainer = Trainer.builder()
                .firstName(firstName)
                .lastName(lastName)
                .password(password)
                .specialization(specialization)
                .build();

        return super.save(newTrainer);
    }
}
