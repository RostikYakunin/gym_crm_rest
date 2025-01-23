package com.crm.mappers;

import com.crm.dtos.UserLoginView;
import com.crm.dtos.trainee.TraineeSaveDto;
import com.crm.dtos.trainee.TraineeUpdateDto;
import com.crm.dtos.trainee.TraineeView;
import com.crm.repositories.entities.Trainee;
import com.crm.repositories.entities.Trainer;
import com.crm.repositories.entities.Training;
import org.mapstruct.*;

import java.util.List;

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
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "trainings", ignore = true)
    Trainee toTrainee(TraineeUpdateDto traineeUpdateDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    UserLoginView toUserLoginView(Trainee trainee);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "active", target = "isActive")
    @Mapping(target = "trainersList", source = "trainings", qualifiedByName = "mapTrainingsToTrainers")
    TraineeView toTraineeView(Trainee trainee);

    @Named("mapTrainingsToTrainers")
    default List<TraineeView.TrainerListView> mapTrainingsToTrainers(List<Training> trainings) {
        if (trainings == null) return null;
        return trainings.stream()
                .map(training -> toTrainerView(training.getTrainer()))
                .toList();
    }

    TraineeView.TrainerListView toTrainerView(Trainer trainer);
}
