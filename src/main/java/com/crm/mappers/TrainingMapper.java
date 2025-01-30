package com.crm.mappers;

import com.crm.dtos.training.TrainingDto;
import com.crm.dtos.training.TrainingView;
import com.crm.repositories.entities.Training;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface TrainingMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    Training toTraining(TrainingDto trainingDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "traineeId", source = "trainee.id")
    @Mapping(target = "trainerId", source = "trainer.id")
    TrainingView toTrainingView(Training training);
}