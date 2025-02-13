package com.crm.converters;

import com.crm.converters.mappers.TrainingMapper;
import com.crm.dtos.training.TrainingDto;
import com.crm.repositories.entities.Training;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TrainingDtoToTrainingConverter implements Converter<TrainingDto, Training> {
    private final TrainingMapper trainingMapper;

    @Override
    public Training convert(TrainingDto trainingDto) {
        return trainingMapper.toTraining(trainingDto);
    }
}