package com.crm.converters;

import com.crm.dtos.trainee.TraineeDto;
import com.crm.repositories.entities.Trainee;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
public class TraineeToTraineeDtoConverter implements Converter<Trainee, TraineeDto> {
    @Override
    public TraineeDto convert(Trainee trainee) {
        var traineeDto = TraineeDto.builder();

        traineeDto.isActive(trainee.isActive());
        traineeDto.id(trainee.getId());
        traineeDto.firstName(trainee.getFirstName());
        traineeDto.lastName(trainee.getLastName());
        traineeDto.userName(trainee.getUserName());
        traineeDto.password(trainee.getPassword());
        traineeDto.dateOfBirth(trainee.getDateOfBirth());
        traineeDto.address(trainee.getAddress());

        var list = trainee.getTrainings();
        if (list != null) {
            traineeDto.trainings(new HashSet<>(list));
        }

        return traineeDto.build();
    }
}
