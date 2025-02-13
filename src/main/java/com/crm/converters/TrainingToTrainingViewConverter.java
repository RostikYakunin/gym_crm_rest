package com.crm.converters;

import com.crm.converters.mappers.TrainingMapper;
import com.crm.dtos.training.TrainingView;
import com.crm.repositories.entities.Training;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TrainingToTrainingViewConverter implements Converter<Training, TrainingView> {
    private final TrainingMapper trainingMapper;

    @Override
    public TrainingView convert(Training source) {
        return trainingMapper.toTrainingView(source);
    }
}
