package com.crm.converters;

import com.crm.converters.mappers.TraineeMapper;
import com.crm.dtos.trainee.TraineeView;
import com.crm.repositories.entities.Trainee;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TraineeToTraineeViewConverter implements Converter<Trainee, TraineeView> {
    private final TraineeMapper traineeMapper;

    @Override
    public TraineeView convert(Trainee source) {
        return traineeMapper.toTraineeView(source);
    }
}
