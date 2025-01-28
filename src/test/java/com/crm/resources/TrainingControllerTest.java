package com.crm.resources;

import com.crm.UnitTestBase;
import com.crm.dtos.training.TrainingDto;
import com.crm.dtos.training.TrainingTypeView;
import com.crm.dtos.training.TrainingView;
import com.crm.mappers.TrainingMapper;
import com.crm.models.TrainingType;
import com.crm.repositories.entities.Trainee;
import com.crm.repositories.entities.Trainer;
import com.crm.repositories.entities.Training;
import com.crm.services.TrainingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


class TrainingControllerTest extends UnitTestBase {
    private MockMvc mockMvc;

    @InjectMocks
    private TrainingController trainingController;

    @Mock
    private TrainingService trainingService;

    @Mock
    private TrainingMapper trainingMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        jsonConverter.setObjectMapper(objectMapper);

        mockMvc = MockMvcBuilders.standaloneSetup(trainingController)
                .setMessageConverters(jsonConverter)
                .build();
    }

    @ParameterizedTest
    @CsvSource({
            "1, 2, Running, YOGA, 2026-01-01T10:00:00, PT1H30M",
            " , 2, Running, YOGA, 2026-01-01T10:00:00, PT1H30M",
            "1,  , Running, YOGA, 2026-01-01T10:00:00, PT1H30M",
            "1, 2, '', YOGA, 2025-01-01T10:00:00, PT1H30M",
            "1, 2, Running, , 2025-01-01T10:00:00, PT1H30M",
            "1, 2, Running, YOGA, 2025-01-01T10:00:00, PT10M"
    })
    @DisplayName("addTraining should handle various inputs and return appropriate status")
    void addTraining_ShouldHandleVariousInputs(
            Long traineeId, Long trainerId, String trainingName, String trainingType, String trainingDate, String trainingDuration
    ) throws Exception {
        // Given
        var trainee = traineeId != null ? testTrainee : null;
        var trainer = trainerId != null ? testTrainer : null;
        var type = trainingType != null ? TrainingType.valueOf(trainingType) : null;
        var date = trainingDate != null ? LocalDateTime.parse(trainingDate) : null;
        var duration = trainingDuration != null ? Duration.parse(trainingDuration) : null;

        var trainingDto = TrainingDto.builder()
                .trainee(trainee)
                .trainer(trainer)
                .trainingName(trainingName)
                .trainingType(type)
                .trainingDate(date)
                .trainingDuration(duration)
                .build();
        var training = new Training();
        var trainingView = new TrainingView();

        if (trainee != null && trainer != null && trainingName != null && !trainingName.isEmpty() &&
                type != null && date != null && duration != null && duration.toMinutes() >= 20 && duration.toHours() <= 2) {
            when(trainingMapper.toTraining(trainingDto)).thenReturn(training);
            when(trainingService.save(training)).thenReturn(training);
            when(trainingMapper.toTrainingView(training)).thenReturn(trainingView);

            mockMvc.perform(post("/api/v1/training")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(trainingDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(trainingView.getId()));
        } else {
            mockMvc.perform(post("/api/v1/training")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(trainingDto)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Test
    @DisplayName("getTrainingTypes should return a list of all relevant training types")
    void getTrainingTypes_ShouldReturnListOfRelevantTrainingTypes() throws Exception {
        //Given - When - Then
        mockMvc.perform(get("/api/v1/training/types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(TrainingType.values().length)))
                .andReturn();
    }
}