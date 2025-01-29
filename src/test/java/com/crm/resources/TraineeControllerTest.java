package com.crm.resources;

import com.crm.UnitTestBase;
import com.crm.dtos.UserLoginDto;
import com.crm.dtos.UserStatusUpdateDto;
import com.crm.dtos.trainee.TraineeSaveDto;
import com.crm.dtos.trainee.TraineeTrainingUpdateDto;
import com.crm.dtos.trainee.TraineeUpdateDto;
import com.crm.dtos.trainee.TraineeView;
import com.crm.dtos.training.TrainingDto;
import com.crm.dtos.training.TrainingShortView;
import com.crm.mappers.TraineeMapper;
import com.crm.mappers.TrainingMapper;
import com.crm.models.TrainingType;
import com.crm.repositories.entities.Trainee;
import com.crm.repositories.entities.Training;
import com.crm.services.TraineeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TraineeControllerTest extends UnitTestBase {

    private MockMvc mockMvc;

    @InjectMocks
    private TraineeController traineeController;

    @Mock
    private TraineeService traineeService;

    @Mock
    private TraineeMapper traineeMapper;

    @Mock
    private TrainingMapper trainingMapper;

    @Captor
    private ArgumentCaptor<String> stringArgumentCaptor;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(traineeController).build();
    }

    @Test
    @DisplayName("Should successfully create trainee")
    void shouldRegisterTraineeSuccessfully() throws Exception {
        // Given
        when(traineeMapper.toTrainee(any(TraineeSaveDto.class))).thenReturn(testTrainee);
        when(traineeService.save(any(Trainee.class))).thenReturn(testTrainee);
        when(traineeMapper.toDto(any(Trainee.class))).thenReturn(testTraineeDto);

        // When - Then
        mockMvc.perform(post("/api/v1/trainee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testTraineeSaveDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userName").value(testTraineeDto.getUserName()))
                .andExpect(jsonPath("$.password").value(testTraineeDto.getPassword()));

        verify(traineeMapper, times(1)).toTrainee(traineeSaveDtoArgumentCaptor.capture());
        verify(traineeService, times(1)).save(traineeArgumentCaptor.capture());
        verify(traineeMapper, times(1)).toDto(traineeArgumentCaptor.capture());
    }

    @ParameterizedTest
    @CsvSource({
            "user1, oldPas1, newPas1, true, 200, 'Password successfully changed'",
            "user2, oldPas2, newPas2, false, 400, 'Password was not changed: inputted password is wrong'"
    })
    @DisplayName("Should successfully change/not change trainee`s password")
    void shouldChangePasswordSuccessfully(
            String username, String oldPassword, String newPassword,
            boolean changeResult, int expectedStatus, String expectedMessage
    ) throws Exception {
        // Given
        var testUserLoginDto = new UserLoginDto(username, oldPassword, newPassword);
        var testFoundTrainee = Trainee.builder()
                .userName(username)
                .password(oldPassword)
                .build();

        when(traineeService.findByUsernameOrThrow(anyString())).thenReturn(testFoundTrainee);
        when(traineeService.changePassword(any(Trainee.class), anyString(), anyString())).thenReturn(changeResult);

        // When - Then
        mockMvc.perform(put("/api/v1/trainee/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUserLoginDto)))
                .andExpect(status().is(expectedStatus))
                .andExpect(content().string(expectedMessage));

        verify(traineeService, times(1)).findByUsernameOrThrow(stringArgumentCaptor.capture());
        verify(traineeService, times(1)).changePassword(
                traineeArgumentCaptor.capture(),
                stringArgumentCaptor.capture(),
                stringArgumentCaptor.capture()
        );
    }

    @ParameterizedTest
    @CsvSource({
            "user1, true, 200",
    })
    @DisplayName("Should get/not get trainee`s profile according to data")
    void shouldGetTraineeProfileSuccessfully(String username, boolean traineeExists, int expectedStatus) throws Exception {
        // Given
        var foundTrainee = traineeExists ? testTrainee : null;

        when(traineeService.findByUsernameOrThrow(anyString())).thenReturn(foundTrainee);
        when(traineeMapper.toTraineeView(any(Trainee.class))).thenReturn(testTraineeView);

        // When - Then
        mockMvc.perform(get("/api/v1/trainee/" + username))
                .andExpect(status().is(expectedStatus))
                .andExpect(result -> {
                    if (traineeExists) {
                        jsonPath("$.userName").value(username);
                    }
                });

        verify(traineeService, times(1)).findByUsernameOrThrow(stringArgumentCaptor.capture());
        verify(traineeMapper, times(1)).toTraineeView(traineeArgumentCaptor.capture());
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
            "user1, true, true, 200",
            "user2, false, false, 400"
    })
    @DisplayName("Should update/not update trainee`s trainings according to data")
    void shouldUpdateTraineeTrainings(String username, boolean traineeExists, boolean validTrainings, int expectedStatus) throws Exception {
        // Given
        var testTrainee = Trainee.builder()
                .id(1L)
                .userName(username)
                .password("somePassword")
                .build();
        var foundTrainee = traineeExists ? testTrainee : null;

        var trainingDto = TrainingDto.builder()
                .trainee(testTrainee)
                .build();

        List<TrainingDto> trainingDtos = validTrainings ? List.of(trainingDto) : Collections.emptyList();
        var updateDto = new TraineeTrainingUpdateDto(username, trainingDtos);

        lenient().when(traineeService.findByUsernameOrThrow(anyString())).thenReturn(foundTrainee);
        lenient().when(trainingMapper.toTraining(any(TrainingDto.class))).thenReturn(new Training());
        lenient().when(traineeService.update(any(Trainee.class))).thenReturn(foundTrainee);

        // When - Then
        if (traineeExists) {
            mockMvc.perform(put("/api/v1/trainee/trainings/")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateDto)))
                    .andExpect(status().is(expectedStatus));

            verify(traineeService, times(1)).findByUsernameOrThrow(stringArgumentCaptor.capture());
            verify(traineeService, times(1)).update(traineeArgumentCaptor.capture());
        } else {
            mockMvc.perform(put("/api/v1/trainee/trainings")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateDto)))
                    .andExpect(status().is(expectedStatus));

            verify(traineeService, never()).findByUsernameOrThrow(stringArgumentCaptor.capture());
            verify(traineeService, never()).update(traineeArgumentCaptor.capture());
        }
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
        var shortView = TrainingShortView.builder()
                .name("Yoga Class")
                .date(LocalDateTime.now())
                .trainerUserName("trainer1")
                .build();

        when(traineeService.findTraineeTrainingsByCriteria(
                eq(username), eq(periodFrom), eq(periodTo), eq(trainerUserName), eq(trainingType)))
                .thenReturn(List.of(new Training()));
        when(trainingMapper.toTrainingShortView(any(Training.class))).thenReturn(shortView);

        // When - Then
        mockMvc.perform(get("/api/v1/trainee/trainings")
                        .param("username", username)
                        .param("period-from", periodFrom != null ? periodFrom.toString() : "")
                        .param("period-to", periodTo != null ? periodTo.toString() : "")
                        .param("trainer-user-name", trainerUserName)
                        .param("training-type", trainingTypeStr != null ? trainingTypeStr : ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value(shortView.getName()))
                .andExpect(jsonPath("$[0].trainerUserName").value(shortView.getTrainerUserName()));
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
            assertThrows(
                    ServletException.class,
                    () -> mockMvc.perform(get("/api/v1/trainee/trainings")
                                    .param("username", username)
                                    .param("training-type", trainingTypeStr))
                            .andExpect(status().is(expectedStatus)),
                    "No enum constant com.crm.models.TrainingType.INVALID_TYPE"
            );
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
                .thenReturn(Collections.emptyList());

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

    @ParameterizedTest
    @CsvSource({
            "user1, user1, 200",
            "user2, user1, 400"
    })
    @DisplayName("Should update/not update trainee according to data")
    void testUpdateTrainee(String existingUsername, String inputUsername, int expectedStatus) throws Exception {
        // Given
        var updateDto = TraineeUpdateDto.builder()
                .firstName("newFirstName")
                .lastName("newLastName")
                .userName(inputUsername)
                .isActive(true)
                .build();

        testTrainee.setUserName(existingUsername);
        when(traineeService.findById(anyLong())).thenReturn(testTrainee);

        if (expectedStatus == 200) {
            var traineeViewDto = TraineeView.builder()
                    .firstName("newFirstName")
                    .lastName("newLastName")
                    .build();

            when(traineeService.update(any(Trainee.class))).thenReturn(testTrainee);
            when(traineeMapper.toTraineeView(any(Trainee.class))).thenReturn(traineeViewDto);
        }

        //When - Then
        mockMvc.perform(put("/api/v1/trainee/" + 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().is(expectedStatus));
    }
}

