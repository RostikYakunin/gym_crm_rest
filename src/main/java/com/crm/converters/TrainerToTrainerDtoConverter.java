package com.crm.converters;

import com.crm.dtos.trainer.TrainerDto;
import com.crm.repositories.entities.Trainer;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class TrainerToTrainerDtoConverter implements Converter<Trainer, TrainerDto> {
    @Override
    public TrainerDto convert(Trainer trainer) {
        var trainerDto = TrainerDto.builder();

        trainerDto.isActive(trainer.isActive());
        trainerDto.id(trainer.getId());
        trainerDto.firstName(trainer.getFirstName());
        trainerDto.lastName(trainer.getLastName());
        trainerDto.userName(trainer.getUserName());
        trainerDto.password(trainer.getPassword());
        trainerDto.specialization(trainer.getSpecialization());

        return trainerDto.build();
    }
}
