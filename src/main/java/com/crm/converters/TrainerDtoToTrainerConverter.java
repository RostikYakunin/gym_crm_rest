package com.crm.converters;

import com.crm.converters.mappers.TrainerMapper;
import com.crm.dtos.trainer.TrainerDto;
import com.crm.repositories.entities.Trainer;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TrainerDtoToTrainerConverter implements Converter<TrainerDto, Trainer> {
    private final TrainerMapper trainerMapper;

    @Override
    public Trainer convert(TrainerDto trainerDto) {
        return trainerMapper.toTrainer(trainerDto);
    }
}
