package com.crm.converters.mappers;

import com.crm.dtos.trainee.TraineeDto;
import com.crm.dtos.trainee.TraineeView;
import com.crm.repositories.entities.Trainee;
import com.crm.repositories.entities.Trainer;
import com.crm.repositories.entities.Training;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface TraineeMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "trainings", ignore = true)
    Trainee toTrainee(TraineeDto traineeDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "isActive", source = "active")
    TraineeDto toDto(Trainee trainee);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "trainings", ignore = true)
    void updateTrainee(@MappingTarget Trainee existingTrainee, Trainee updatedTrainee);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "active", target = "isActive")
    @Mapping(target = "trainersList", source = "trainings", qualifiedByName = "mapTrainingsToTrainers")
    TraineeView toTraineeView(Trainee trainee);

    @Named("mapTrainingsToTrainers")
    default Set<TraineeView.TrainerListView> mapTrainingsToTrainers(List<Training> trainings) {
        if (trainings == null) return null;
        return trainings.stream()
                .map(training -> toTrainerView(training.getTrainer()))
                .collect(Collectors.toSet());
    }

    TraineeView.TrainerListView toTrainerView(Trainer trainer);
}
