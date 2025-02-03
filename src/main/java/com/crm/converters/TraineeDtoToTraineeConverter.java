package com.crm.converters;

import com.crm.dtos.trainee.TraineeDto;
import com.crm.repositories.entities.Trainee;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class TraineeDtoToTraineeConverter implements Converter<TraineeDto, Trainee> {
    @Override
    public Trainee convert(TraineeDto traineeDto) {
        var trainee = Trainee.builder();

        trainee.id(traineeDto.getId());
        trainee.firstName(traineeDto.getFirstName());
        trainee.lastName(traineeDto.getLastName());
        trainee.userName(traineeDto.getUserName());
        trainee.password(traineeDto.getPassword());
        trainee.isActive(traineeDto.getIsActive());
        trainee.dateOfBirth(traineeDto.getDateOfBirth());
        trainee.address(traineeDto.getAddress());

        return trainee.build();
    }
}
