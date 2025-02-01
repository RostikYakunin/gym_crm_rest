package com.crm.converters;

import com.crm.dtos.training.TrainingDto;
import com.crm.repositories.entities.Training;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class TrainingDtoToTrainingConverter implements Converter<TrainingDto, Training> {
    @Override
    public Training convert(TrainingDto trainingDto) {
        Training.TrainingBuilder training = Training.builder();

        training.trainee(trainingDto.getTrainee());
        training.trainer(trainingDto.getTrainer());
        training.trainingName(trainingDto.getTrainingName());
        training.trainingType(trainingDto.getTrainingType());
        training.trainingDate(trainingDto.getTrainingDate());
        training.trainingDuration(trainingDto.getTrainingDuration());

        return training.build();
    }
}