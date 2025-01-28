package com.crm.mappers;

import com.crm.dtos.trainee.TraineeDto;
import com.crm.dtos.trainee.TraineeSaveDto;
import com.crm.dtos.trainee.TraineeUpdateDto;
import com.crm.dtos.trainee.TraineeViewDto;
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
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userName", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "trainings", ignore = true)
    @Mapping(target = "isActive", constant = "true")
    Trainee toTrainee(TraineeSaveDto traineeSaveDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "isActive", source = "active")
    TraineeDto toDto(Trainee trainee);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "trainings", ignore = true)
    void updateTrainee(@MappingTarget Trainee existingTrainee, Trainee updatedTrainee);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "trainings", ignore = true)
    Trainee toTrainee(TraineeUpdateDto traineeUpdateDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "active", target = "isActive")
    @Mapping(target = "trainersList", source = "trainings", qualifiedByName = "mapTrainingsToTrainers")
    TraineeViewDto toTraineeView(Trainee trainee);

    @Named("mapTrainingsToTrainers")
    default Set<TraineeViewDto.TrainerListView> mapTrainingsToTrainers(List<Training> trainings) {
        if (trainings == null) return null;
        return trainings.stream()
                .map(training -> toTrainerView(training.getTrainer()))
                .collect(Collectors.toSet());
    }

    TraineeViewDto.TrainerListView toTrainerView(Trainer trainer);
}
