package com.crm.converters;

import com.crm.dtos.trainee.TraineeView;
import com.crm.repositories.entities.Trainee;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class TraineeToTraineeViewConverter implements Converter<Trainee, TraineeView> {
    @Override
    public TraineeView convert(Trainee source) {
        var trainersList = (source.getTrainings() == null) ?
                null :
                source.getTrainings().stream()
                        .map(training -> new TraineeView.TrainerListView(
                                training.getTrainer().getUserName(),
                                training.getTrainer().getFirstName(),
                                training.getTrainer().getLastName(),
                                training.getTrainer().getSpecialization()
                        ))
                        .collect(Collectors.toSet());

        return new TraineeView(
                source.getFirstName(),
                source.getLastName(),
                source.getDateOfBirth(),
                source.getAddress(),
                source.isActive(),
                trainersList
        );
    }
}
