package com.crm.converters;

import com.crm.converters.mappers.TrainerMapper;
import com.crm.dtos.trainer.TrainerView;
import com.crm.repositories.entities.Trainer;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TrainerToTrainerViewConverter implements Converter<Trainer, TrainerView> {
    private final TrainerMapper trainerMapper;

    @Override
    public TrainerView convert(Trainer trainer) {
        return trainerMapper.toTrainerView(trainer);
    }
}
