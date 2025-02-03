package com.crm.converters;

import com.crm.dtos.trainer.TrainerView;
import com.crm.dtos.training.TrainingView;
import com.crm.repositories.entities.Trainer;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class TrainerToTrainerViewConverter implements Converter<Trainer, TrainerView> {
    @Override
    public TrainerView convert(Trainer trainer) {
        var trainingViews = (trainer.getTrainings() == null) ?
                null :
                trainer.getTrainings().stream()
                        .map(training ->
                                TrainingView.builder()
                                        .traineeId(training.getTrainee().getId())
                                        .trainerId(training.getTrainer().getId())
                                        .id(training.getId())
                                        .trainingName(training.getTrainingName())
                                        .trainingType(training.getTrainingType())
                                        .trainingDate(training.getTrainingDate())
                                        .trainingDuration(training.getTrainingDuration())
                                        .build()
                        )
                        .collect(Collectors.toSet());

        return TrainerView.builder()
                .isActive(trainer.isActive())
                .trainingViews(trainingViews)
                .firstName(trainer.getFirstName())
                .lastName(trainer.getLastName())
                .userName(trainer.getUserName())
                .specialization(trainer.getSpecialization())
                .build();
    }
}
