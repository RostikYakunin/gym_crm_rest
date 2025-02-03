package com.crm.converters.mappers;

import com.crm.repositories.entities.Trainee;
import com.crm.repositories.entities.Trainer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserUpdateMapper {
    @Mapping(target = "trainings", ignore = true)
    void updateTrainee(@MappingTarget Trainee existingTrainee, Trainee updatedTrainee);

    @Mapping(target = "trainings", ignore = true)
    void updateTrainer(@MappingTarget Trainer existingTrainer, Trainer updatedTrainer);
}
