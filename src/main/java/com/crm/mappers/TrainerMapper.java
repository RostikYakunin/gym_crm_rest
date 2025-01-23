package com.crm.mappers;

import com.crm.dtos.UserLoginView;
import com.crm.dtos.trainer.*;
import com.crm.repositories.entities.Trainee;
import com.crm.repositories.entities.Trainer;
import com.crm.repositories.entities.Training;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TrainerMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(source = "isActive", target = "isActive")
    @Mapping(source = "username", target = "userName")
    @Mapping(target = "trainings", ignore = true)
    Trainer toTrainer(TrainerDto trainerDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    UserLoginView toUserLoginView(Trainer trainer);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "active", target = "isActive")
    @Mapping(target = "traineesList", source = "trainings", qualifiedByName = "mapTrainingsToTraineesList")
    TrainerView toTrainerView(Trainer trainer);

    @Named("mapTrainingsToTraineesList")
    default List<TrainerView.TraineeListView> mapTrainingsToTrainers(List<Training> trainings) {
        if (trainings == null) return null;
        return trainings.stream()
                .map(training -> toTraineeListView(training.getTrainee()))
                .toList();
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "trainings", ignore = true)
    Trainer toTrainer(TrainerUpdateDto updateDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    TrainerShortView toTrainerShortView(Trainer trainer);

    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "userName", source = "userName")
    TrainerView.TraineeListView toTraineeListView(Trainee trainee);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "specialization", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "userName", ignore = true)
    @Mapping(target = "trainings", ignore = true)
    Trainer toTrainer(TrainerSaveDto trainerDto);
}