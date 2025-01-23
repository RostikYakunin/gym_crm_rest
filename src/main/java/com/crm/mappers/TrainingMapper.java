package com.crm.mappers;

import com.crm.dtos.training.TrainingDto;
import com.crm.dtos.training.TrainingShortView;
import com.crm.dtos.training.TrainingTypeView;
import com.crm.models.TrainingType;
import com.crm.repositories.entities.Training;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface TrainingMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "trainingName", target = "name")
    @Mapping(source = "trainingDate", target = "date")
    @Mapping(source = "trainingType", target = "type")
    @Mapping(source = "trainingDuration", target = "duration")
    @Mapping(target = "traineeUserName", expression = "java(training.getTrainee().getUserName())")
    TrainingShortView toTrainingShortView(Training training);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    Training toTraining(TrainingDto trainingDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    TrainingTypeView toTrainingTypeView(TrainingType training);
}