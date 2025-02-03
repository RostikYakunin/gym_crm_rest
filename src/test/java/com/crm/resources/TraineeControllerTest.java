package com.crm.resources;

import com.crm.UnitTestBase;
import com.crm.dtos.UserLoginDto;
import com.crm.dtos.UserStatusUpdateDto;
import com.crm.dtos.trainee.TraineeDto;
import com.crm.dtos.trainee.TraineeTrainingUpdateDto;
import com.crm.dtos.training.TrainingDto;
import com.crm.dtos.training.TrainingView;
import com.crm.enums.TrainingType;
import com.crm.init.DataInitializer;
import com.crm.repositories.entities.Trainee;
import com.crm.services.TraineeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class TraineeControllerTest extends UnitTestBase {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private TraineeService traineeService;
    @MockitoBean
    private DataInitializer dataInitializer;
    @Autowired
    private ObjectMapper objectMapper;

    @Captor
    private ArgumentCaptor<String> stringArgumentCaptor;

    @Captor
    private ArgumentCaptor<TraineeDto> traineeDtoArgumentCaptor;

    @Captor
    private ArgumentCaptor<UserLoginDto> userLoginDtoArgumentCaptor;

    @Captor
    private ArgumentCaptor<TraineeTrainingUpdateDto> traineeTrainingUpdateDtoArgumentCaptor;

    @Test
    @DisplayName("Should successfully create trainee")
    void shouldRegisterTraineeSuccessfully() throws Exception {
        // Given
        when(traineeService.addTrainee(any(TraineeDto.class))).thenReturn(testTraineeDto);

        // When - Then
        mockMvc.perform(post("/api/v1/trainee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testTraineeDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userName").value(testTraineeDto.getUserName()))
                .andExpect(jsonPath("$.password").value(testTraineeDto.getPassword()));

        verify(traineeService, times(1)).addTrainee(traineeDtoArgumentCaptor.capture());
    }

    @ParameterizedTest
    @CsvSource({
            "user1, oldPas1, newPas1, 200, 'Password successfully changed'",
    })
    @DisplayName("Should successfully change/not change trainee`s password")
    void shouldChangePasswordSuccessfully(
            String username, String oldPassword, String newPassword,
            int expectedStatus, String expectedMessage
    ) throws Exception {
        // Given
        var testUserLoginDto = new UserLoginDto(username, oldPassword, newPassword);
        doNothing().when(traineeService).changePassword(any(UserLoginDto.class));

        // When - Then
        mockMvc.perform(put("/api/v1/trainee/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUserLoginDto)))
                .andExpect(status().is(expectedStatus))
                .andExpect(content().string(expectedMessage));

        verify(traineeService, times(1)).changePassword(userLoginDtoArgumentCaptor.capture());
    }

    @ParameterizedTest
    @CsvSource({
            "user1, true, 200",
    })
    @DisplayName("Should get/not get trainee`s profile according to data")
    void shouldGetTraineeProfileSuccessfully(String username, boolean traineeExists, int expectedStatus) throws Exception {
        // Given
        when(traineeService.findProfileByUserName(anyString())).thenReturn(testTraineeView);

        // When - Then
        mockMvc.perform(get("/api/v1/trainee/" + username))
                .andExpect(status().is(expectedStatus))
                .andExpect(result -> {
                    if (traineeExists) {
                        jsonPath("$.userName").value(username);
                    }
                });

        verify(traineeService, times(1)).findProfileByUserName(stringArgumentCaptor.capture());
    }

    @ParameterizedTest
    @CsvSource({
            "user1, true, 200",
    })
    @DisplayName("Should delete/not delete trainee`s profile according to data")
    void shouldDeleteTraineeSuccessfully(String username, boolean traineeExists, int expectedStatus) throws Exception {
        // Given
        var testTrainee = Trainee.builder()
                .id(1L)
                .userName(username)
                .password("somePassword")
                .build();
        when(traineeService.findByUsernameOrThrow(anyString())).thenReturn(testTrainee);

        // When - Then
        if (traineeExists) {
            var result = mockMvc.perform(delete("/api/v1/trainee/" + username))
                    .andExpect(status().is(expectedStatus))
                    .andReturn();

            var expectedMessage = "Trainee with userName=" + username + " was deleted";
            assertTrue(result.getResponse().getContentAsString().contains(expectedMessage));

            verify(traineeService, times(1)).findByUsernameOrThrow(stringArgumentCaptor.capture());
            verify(traineeService, times(1)).delete(traineeArgumentCaptor.capture());
        } else {
            assertThrows(
                    BadRequestException.class,
                    () -> mockMvc.perform(delete("/api/v1/trainee/" + username))
                            .andExpect(status().isBadRequest())
                            .andReturn(),
                    "Trainee with user name= " + username + " was not found"
            );

            verify(traineeService, times(1)).findByUsernameOrThrow(stringArgumentCaptor.capture());
            verify(traineeService, never()).delete(any());
        }
    }

    @ParameterizedTest
    @CsvSource({
            "user1, 200"
    })
    @DisplayName("Should update/not update trainee`s trainings according to data")
    void shouldUpdateTraineeTrainings(String username, int expectedStatus) throws Exception {
        // Given
        var trainingDto = TrainingDto.builder()
                .trainee(testTrainee)
                .build();

        var updateDto = new TraineeTrainingUpdateDto(username, List.of(trainingDto));
        when(traineeService.updateTraineeTrainings(any(TraineeTrainingUpdateDto.class))).thenReturn(Set.of(new TrainingView()));

        // When - Then
        mockMvc.perform(put("/api/v1/trainee/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().is(expectedStatus));

        verify(traineeService, times(1)).updateTraineeTrainings(traineeTrainingUpdateDtoArgumentCaptor.capture());
    }

    @ParameterizedTest
    @CsvSource({
            "user1, 2023-01-01, 2023-01-07, trainer1, YOGA",
            "user2, 2023-02-01, 2023-02-28, trainer2, YOGA",
            "user3, , , , YOGA"
    })
    @DisplayName("Should return trainee`s training list according to data")
    void shouldReturnTrainingListWithValidCriteria(String username, String periodFromStr, String periodToStr, String trainerUserName, String trainingTypeStr) throws Exception {
        // Given
        var periodFrom = periodFromStr == null ? null : LocalDate.parse(periodFromStr);
        var periodTo = periodToStr == null ? null : LocalDate.parse(periodToStr);
        var trainingType = trainingTypeStr == null || trainingTypeStr.isEmpty() ? null : TrainingType.valueOf(trainingTypeStr);
        var shortView = TrainingView.builder()
                .trainingName("Yoga Class")
                .trainingDate(LocalDateTime.now())
                .trainerId(1L)
                .build();

        when(traineeService.findTraineeTrainingsByCriteria(
                eq(username), eq(periodFrom), eq(periodTo), eq(trainerUserName), eq(trainingType)))
                .thenReturn(Set.of(shortView));

        // When - Then
        mockMvc.perform(get("/api/v1/trainee/trainings")
                        .param("username", username)
                        .param("period-from", periodFrom != null ? periodFrom.toString() : "")
                        .param("period-to", periodTo != null ? periodTo.toString() : "")
                        .param("trainer-user-name", trainerUserName)
                        .param("training-type", trainingTypeStr != null ? trainingTypeStr : ""))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].trainingName").value(shortView.getTrainingName()))
                .andExpect(jsonPath("$[0].trainerId").value(shortView.getTrainerId()));
    }

    @ParameterizedTest
    @CsvSource({
            "INVALID_TYPE, 400",
            "YOGA, 200",
            "YOGA, 200"
    })
    @DisplayName("Should return response according to data")
    void shouldReturnBadRequestWhenInvalidTrainingType(String trainingTypeStr, Integer expectedStatus) throws Exception {
        // Given
        String username = "user1";

        // When - Then
        if (!expectedStatus.equals(400)) {
            mockMvc.perform(get("/api/v1/trainee/trainings")
                            .param("username", username)
                            .param("training-type", trainingTypeStr))
                    .andExpect(status().is(expectedStatus));
        } else {
            mockMvc.perform(get("/api/v1/trainee/trainings")
                            .param("username", username)
                            .param("training-type", trainingTypeStr))
                    .andExpect(status().is(expectedStatus));
        }
    }

    @ParameterizedTest
    @CsvSource({
            "user1, 2023-01-01, 2023-01-07",
            "user2, 2023-02-01, 2023-02-28"
    })
    @DisplayName("Should return empty list according to data")
    void shouldReturnEmptyListWhenNoTrainingsFound(String username, String periodFromStr, String periodToStr) throws Exception {
        var periodFrom = LocalDate.parse(periodFromStr);
        var periodTo = LocalDate.parse(periodToStr);

        when(traineeService.findTraineeTrainingsByCriteria(eq(username), eq(periodFrom), eq(periodTo), eq(null), eq(null)))
                .thenReturn(Collections.emptySet());

        // When - Then
        mockMvc.perform(get("/api/v1/trainee/trainings")
                        .param("username", username)
                        .param("period-from", periodFrom.toString())
                        .param("period-to", periodTo.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @ParameterizedTest
    @CsvSource({
            "user1, true, 200, 'Trainee with username=user1 was activated.'",
            "user2, false, 200, 'Trainee with username=user2 was deactivated.'",
    })
    @DisplayName("Should update/not update trainee`s status according to data")
    void shouldUpdateTraineeStatusWithValidData(String username, boolean isActive, int expectedStatus, String expectedMessage) throws Exception {
        // Given
        var statusUpdateDto = new UserStatusUpdateDto(username, isActive);
        testTrainee.setUserName(username);

        when(traineeService.findByUsernameOrThrow(username)).thenReturn(testTrainee);
        if (isActive) {
            when(traineeService.activateStatus(testTrainee.getId())).thenReturn(true);
        } else {
            when(traineeService.deactivateStatus(testTrainee.getId())).thenReturn(false);
        }

        // When - Then
        mockMvc.perform(patch("/api/v1/trainee/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusUpdateDto)))
                .andExpect(status().is(expectedStatus))
                .andExpect(content().string(expectedMessage));
    }

    @Test
    @DisplayName("Should update trainee according to data")
    void testUpdateTrainee() throws Exception {
        // Given
        when(traineeService.updateTraineeProfile(anyLong(), any(TraineeDto.class))).thenReturn(testTraineeView);

        //When - Then
        mockMvc.perform(put("/api/v1/trainee/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testTraineeDto)))
                .andExpect(status().isOk())
                .andDo(print());
    }
}

