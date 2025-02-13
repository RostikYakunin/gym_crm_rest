package com.crm.converters;

import com.crm.converters.mappers.TraineeMapper;
import com.crm.dtos.trainee.TraineeDto;
import com.crm.repositories.entities.Trainee;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TraineeToTraineeDtoConverter implements Converter<Trainee, TraineeDto> {
    private final TraineeMapper traineeMapper;

    @Override
    public TraineeDto convert(Trainee trainee) {
        return traineeMapper.toDto(trainee);
    }
}
