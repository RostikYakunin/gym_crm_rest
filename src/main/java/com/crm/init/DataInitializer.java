package com.crm.init;

import com.crm.repositories.entities.Trainee;
import com.crm.repositories.entities.Trainer;
import com.crm.repositories.entities.Training;
import com.crm.services.TraineeService;
import com.crm.services.TrainerService;
import com.crm.services.TrainingService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {
    @Value("${data.file.trainee_data}")
    private String traineeDataFilePath;
    @Value("${data.file.trainer_data}")
    private String trainerDataFilePath;
    @Value("${data.file.training_data}")
    private String trainingDataFilePath;

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;
    private final ObjectMapper objectMapper;

    @PostConstruct
    public void initializeData() {
        try {
            log.info("TraineeData`s initialization started ...");
            initializeTraineeData();
            log.info("TraineeData`s initialization successfully completed");

            log.info("TrainerData`s initialization started ...");
            initializeTrainerData();
            log.info("TrainerData`s initialization successfully completed");

            log.info("TrainingData`s initialization started ...");
            initializeTrainingData();
            log.info("TrainingData`s initialization successfully completed");
        } catch (Exception e) {
            log.error("Data initialization failed ...");
            throw new RuntimeException("Something went wrong with file deserialization", e);
        }
    }

    private void initializeTraineeData() throws Exception {
        List<Trainee> trainees = objectMapper.readValue(new File(traineeDataFilePath), new TypeReference<>() {
        });
        trainees.forEach(traineeService::save);
    }

    private void initializeTrainerData() throws Exception {
        List<Trainer> trainers = objectMapper.readValue(new File(trainerDataFilePath), new TypeReference<>() {
        });
        trainers.forEach(trainerService::save);
    }

    private void initializeTrainingData() throws Exception {
        List<Training> trainings = objectMapper.readValue(new File(trainingDataFilePath), new TypeReference<>() {
        });
        trainings.forEach(trainingService::save);
    }
}
