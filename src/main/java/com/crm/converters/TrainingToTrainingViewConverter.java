package com.crm.converters;

import com.crm.dtos.training.TrainingView;
import com.crm.repositories.entities.Training;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class TrainingToTrainingViewConverter implements Converter<Training, TrainingView> {
    @Override
    public TrainingView convert(Training source) {
        return new TrainingView(
                source.getId(),
                source.getTrainee() != null ? source.getTrainee().getId() : null,
                source.getTrainer() != null ? source.getTrainer().getId() : null,
                source.getTrainingName(),
                source.getTrainingType(),
                source.getTrainingDate(),
                source.getTrainingDuration()
        );
    }
}
