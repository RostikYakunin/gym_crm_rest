package com.crm.resources;

import com.crm.dtos.UserLoginDto;
import com.crm.dtos.UserStatusUpdateDto;
import com.crm.dtos.trainee.*;
import com.crm.dtos.training.TrainingShortView;
import com.crm.mappers.TraineeMapper;
import com.crm.mappers.TrainingMapper;
import com.crm.models.TrainingType;
import com.crm.services.TraineeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/trainee")
@RequiredArgsConstructor
@Tag(name = "REST API for Trainee", description = "Provides resource methods for managing trainees")
public class TraineeController {
    private final TraineeService traineeService;
    private final TraineeMapper traineeMapper;
    private final TrainingMapper trainingMapper;

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
    public ResponseEntity<TraineeDto> registerTrainee(@RequestBody @Valid TraineeSaveDto traineeDto) {
        var trainee = traineeMapper.toTrainee(traineeDto);
        var savedTrainee = traineeService.save(trainee);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(traineeMapper.toDto(savedTrainee));
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
        var foundTrainee = traineeService.findByUsernameOrThrow(loginDto.getUserName());
        var result = traineeService.changePassword(
                foundTrainee,
                loginDto.getOldPassword(),
                loginDto.getNewPassword()
        );

        return result
                ? ResponseEntity.ok("Password successfully changed")
                : ResponseEntity.badRequest().body("Password was not changed: inputted password is wrong");
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
    public ResponseEntity<TraineeView> getTraineeProfile(@PathVariable("username") String username) {
        var foundTrainee = traineeService.findByUsernameOrThrow(username);
        return ResponseEntity.ok(traineeMapper.toTraineeView(foundTrainee));
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
            @RequestBody @Valid TraineeUpdateDto updateDto
    ) {
        var existingTrainee = Optional.ofNullable(traineeService.findById(id))
                .orElseThrow(() -> new EntityNotFoundException("Trainee with user name=" + updateDto.getUserName() + " not found."));

        if (!existingTrainee.getUserName().equals(updateDto.getUserName())) {
            return ResponseEntity.badRequest().build();
        }

        var fromDto = traineeMapper.toTrainee(updateDto);
        traineeMapper.updateTrainee(existingTrainee, fromDto);

        var updatedTrainee = traineeService.update(existingTrainee);
        return updatedTrainee != null
                ? ResponseEntity.ok(traineeMapper.toTraineeView(updatedTrainee))
                : ResponseEntity.notFound().build();
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
    public ResponseEntity<Set<TrainingShortView>> updateTraineeTrainings(@RequestBody @Valid TraineeTrainingUpdateDto updateDto) throws BadRequestException {
        var foundTrainee = traineeService.findByUsernameOrThrow(updateDto.getUserName());

        boolean containsInvalidTrainings = updateDto.getTrainings()
                .stream()
                .anyMatch(trainingDto -> !trainingDto.getTrainee().getId().equals(foundTrainee.getId()));

        if (containsInvalidTrainings) {
            return ResponseEntity.badRequest().build();
        }

        var newTrainings = updateDto.getTrainings()
                .stream()
                .map(trainingMapper::toTraining)
                .collect(Collectors.toSet());

        foundTrainee.getTrainings().addAll(newTrainings);

        return ResponseEntity.ok(
                traineeService.update(foundTrainee)
                        .getTrainings()
                        .stream()
                        .map(trainingMapper::toTrainingShortView)
                        .collect(Collectors.toSet())
        );
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
    public ResponseEntity<Set<TrainingShortView>> getTraineeTrainings(
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
                                trainingType != null ? TrainingType.valueOf(trainingType) : null
                        )
                        .stream()
                        .map(trainingMapper::toTrainingShortView)
                        .collect(Collectors.toSet())
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
