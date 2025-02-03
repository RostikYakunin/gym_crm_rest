package com.crm.resources;

import com.crm.UnitTestBase;
import com.crm.dtos.training.TrainingDto;
import com.crm.dtos.training.TrainingView;
import com.crm.enums.TrainingType;
import com.crm.init.DataInitializer;
import com.crm.services.TrainingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class TrainingControllerTest extends UnitTestBase {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private TrainingService trainingService;
    @MockitoBean
    private DataInitializer dataInitializer;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("addTraining should handle various inputs and return appropriate status")
    void addTraining_ShouldHandleVariousInputs() throws Exception {
        // Given
        var trainingDto = TrainingDto
                .builder()
                .trainee(testTrainee)
                .trainer(testTrainer)
                .trainingDate(LocalDateTime.now().plusHours(1))
                .trainingDuration(Duration.ofMinutes(30))
                .trainingType(TrainingType.FITNESS)
                .trainingName("Trainings")
                .build();

        var trainingView = TrainingView.builder()
                .trainerId(1L)
                .traineeId(1L)
                .trainingName("TestName")
                .build();

        when(trainingService.addTraining(any(TrainingDto.class))).thenReturn(trainingView);

        // When - Then
        mockMvc.perform(post("/api/v1/training")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trainingDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(trainingView.getId()));
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