package com.crm.resources;

import com.crm.dtos.UserLoginDto;
import com.crm.dtos.UserStatusUpdateDto;
import com.crm.dtos.trainee.TraineeDto;
import com.crm.dtos.trainee.TraineeTrainingUpdateDto;
import com.crm.dtos.trainee.TraineeView;
import com.crm.dtos.training.TrainingView;
import com.crm.enums.TrainingType;
import com.crm.services.TraineeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/trainee")
@RequiredArgsConstructor
@Tag(name = "REST API for Trainee", description = "Provides resource methods for managing trainees")
public class TraineeController {
    private final TraineeService traineeService;

    @Operation(
            summary = "Register a new trainee",
            description = "Creates a new trainee account and returns credentials.",
            parameters = {
                    @Parameter(name = "traineeDto", description = "TraineeDto object", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "201", description = "Trainee created"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @PostMapping
    public ResponseEntity<TraineeDto> registerTrainee(@RequestBody @Valid TraineeDto traineeDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(traineeService.addTrainee(traineeDto));
    }

    @Operation(
            summary = "Change trainee`s password.",
            description = "Updates the password for a given trainee`s username.",
            parameters = {
                    @Parameter(name = "loginDto", description = "LoginRequestDto object.", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Password changed"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @PutMapping("/password")
    public ResponseEntity<String> changePassword(@RequestBody @Valid UserLoginDto loginDto) {
        traineeService.changePassword(loginDto);
        return ResponseEntity.ok("Password successfully changed");
    }

    @Operation(
            summary = "Get trainee profile",
            description = "Retrieves trainee details by username.",
            parameters = {
                    @Parameter(name = "username", description = "Trainee`s username.", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Profile retrieved successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @GetMapping("/{username}")
    public ResponseEntity<TraineeView> findTraineeProfile(@PathVariable("username") String username) {
        return ResponseEntity.ok(traineeService.findProfileByUserName(username));
    }

    @Operation(
            summary = "Update trainee`s profile",
            description = "Modifies trainee details.",
            parameters = {
                    @Parameter(name = "updateDto", description = "TraineeUpdateDto object.", required = true),
                    @Parameter(name = "id", description = "Trainee`s id.", required = true),
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<TraineeView> updateTrainee(
            @PathVariable("id") Long id,
            @RequestBody @Valid TraineeDto updateDto
    ) {
        return ResponseEntity.ok(traineeService.updateTraineeProfile(id, updateDto));
    }

    @Operation(
            summary = "Delete trainee profile",
            description = "Removes a trainee account.",
            parameters = {
                    @Parameter(name = "updateDto", description = "TraineeUpdateDto object.", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Trainee profile deleted successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @DeleteMapping("/{username}")
    public ResponseEntity<String> deleteTrainee(@PathVariable("username") String username) {
        var foundTrainee = traineeService.findByUsernameOrThrow(username);

        traineeService.delete(foundTrainee);
        return ResponseEntity.ok(
                String.format("Trainee with userName=%s was deleted", username)
        );
    }

    @Operation(
            summary = "Update trainee's training list",
            parameters = {
                    @Parameter(name = "updateDto", description = "TraineeUpdateDto object.", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Trainee profile deleted successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @PutMapping("/trainings")
    public ResponseEntity<Set<TrainingView>> updateTraineeTrainings(@RequestBody @Valid TraineeTrainingUpdateDto updateDto) {
        return ResponseEntity.ok(traineeService.updateTraineeTrainings(updateDto));
    }

    @Operation(
            summary = "Get trainee trainings list",
            description = "Retrieves training`s list by trainee`s username",
            parameters = {
                    @Parameter(name = "username", description = "Trainee`s username.", required = true),
                    @Parameter(name = "periodFrom", description = "Criteria - period from."),
                    @Parameter(name = "periodTo", description = "Criteria - period to."),
                    @Parameter(name = "trainerUserName", description = "Trainer`s user name."),
                    @Parameter(name = "trainingType", description = "Training type.")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Trainee profile deleted successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @GetMapping("/trainings")
    public ResponseEntity<Set<TrainingView>> getTraineeTrainings(
            @RequestParam("username") String username,
            @RequestParam(name = "period-from", required = false) LocalDate periodFrom,
            @RequestParam(name = "period-to", required = false) LocalDate periodTo,
            @RequestParam(name = "trainer-user-name", required = false) String trainerUserName,
            @RequestParam(name = "training-type", required = false) String trainingType
    ) {
        return ResponseEntity.ok(
                traineeService.findTraineeTrainingsByCriteria(
                        username,
                        periodFrom,
                        periodTo,
                        trainerUserName,
                        Optional.ofNullable(trainingType).map(TrainingType::valueOf).orElse(null)
                )
        );
    }

    @Operation(
            summary = "Activate/De-Activate Trainee",
            description = "Toggle current trainee`s status to chosen.",
            parameters = {
                    @Parameter(name = "statusUpdateDto", description = "UserStatusUpdateDto object", required = true),
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Trainee profile deleted successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @PatchMapping("/status")
    public ResponseEntity<String> updateTraineeStatus(@RequestBody @Valid UserStatusUpdateDto statusUpdateDto) {
        var foundTrainee = traineeService.findByUsernameOrThrow(statusUpdateDto.getUserName());
        var isActive = statusUpdateDto.getIsActive()
                ? traineeService.activateStatus(foundTrainee.getId())
                : traineeService.deactivateStatus(foundTrainee.getId());

        return ResponseEntity.ok(
                "Trainee with username=" + foundTrainee.getUserName() +
                        (isActive ? " was activated." : " was deactivated.")
        );
    }
}
