package com.crm.converters;

import com.crm.dtos.trainer.TrainerDto;
import com.crm.repositories.entities.Trainer;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class TrainerDtoToTrainerConverter implements Converter<TrainerDto, Trainer> {
    @Override
    public Trainer convert(TrainerDto trainerDto) {
        var trainer = Trainer.builder();

        trainer.id(trainerDto.getId());
        trainer.firstName(trainerDto.getFirstName());
        trainer.lastName(trainerDto.getLastName());
        trainer.userName(trainerDto.getUserName());
        trainer.password(trainerDto.getPassword());
        if (trainerDto.getIsActive() != null) {
            trainer.isActive(trainerDto.getIsActive());
        }
        trainer.specialization(trainerDto.getSpecialization());

        return trainer.build();
    }
}
