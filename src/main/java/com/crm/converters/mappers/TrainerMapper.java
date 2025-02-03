package com.crm.converters.mappers;

import com.crm.dtos.trainer.TrainerDto;
import com.crm.dtos.trainer.TrainerView;
import com.crm.dtos.training.TrainingView;
import com.crm.repositories.entities.Trainer;
import com.crm.repositories.entities.Training;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface TrainerMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "active", target = "isActive")
    @Mapping(target = "trainingViews", source = "trainings")
    TrainerView toTrainerView(Trainer trainer);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "traineeId", source = "trainee.id")
    @Mapping(target = "trainerId", source = "trainer.id")
    TrainingView toTrainingView(Training training);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "trainings", ignore = true)
    Trainer toTrainer(TrainerDto trainerDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "trainings", ignore = true)
    void updateTrainer(@MappingTarget Trainer existingTrainer, Trainer updatedTrainer);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "isActive", source = "active")
    TrainerDto toDto(Trainer trainer);
}