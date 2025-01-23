package com.crm.resources;

import com.crm.dtos.UserLoginDto;
import com.crm.dtos.UserLoginView;
import com.crm.dtos.UserStatusUpdateDto;
import com.crm.dtos.trainee.TraineeSaveDto;
import com.crm.dtos.trainee.TraineeTrainerUpdateDto;
import com.crm.dtos.trainee.TraineeUpdateDto;
import com.crm.dtos.trainee.TraineeView;
import com.crm.dtos.trainer.TrainerDto;
import com.crm.dtos.training.TrainingShortView;
import com.crm.mappers.TraineeMapper;
import com.crm.mappers.TrainingMapper;
import com.crm.models.TrainingType;
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
import java.util.List;

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
    public ResponseEntity<UserLoginView> registerTrainee(@RequestBody @Valid TraineeSaveDto traineeDto) {
        var trainee = traineeMapper.toTrainee(traineeDto);
        var savedTrainee = traineeService.save(trainee);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(traineeMapper.toUserLoginView(savedTrainee));
    }

    @Operation(
            summary = "Trainee login",
            description = "Authenticates a trainee based on username and password.",
            parameters = {
                    @Parameter(name = "username", description = "Trainee`s username.", required = true),
                    @Parameter(name = "password", description = "Trainee`s password.", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Login successful"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @GetMapping("/login")
    public ResponseEntity<String> login(
            @RequestParam("username") String username,
            @RequestParam("password") String password
    ) {
        var isValid = traineeService.isUsernameAndPasswordMatching(username, password);
        return isValid ? ResponseEntity.ok().build() : ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
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
    @PutMapping("/login")
    public ResponseEntity<Boolean> changePassword(@RequestBody @Valid UserLoginDto loginDto) {
        var foundTrainee = traineeService.findByUsername(loginDto.getUserName());
        var result = traineeService.changePassword(
                foundTrainee,
                loginDto.getOldPassword(),
                loginDto.getNewPassword()
        );

        return ResponseEntity.ok(result);
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
        var foundTrainee = traineeService.findByUsername(username);
        return foundTrainee != null
                ? ResponseEntity.ok(traineeMapper.toTraineeView(foundTrainee))
                : ResponseEntity.notFound().build();
    }

    @Operation(
            summary = "Update trainee`s profile",
            description = "Modifies trainee details.",
            parameters = {
                    @Parameter(name = "updateDto", description = "TraineeUpdateDto object.", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @PutMapping
    public ResponseEntity<TraineeView> updateTrainee(@RequestBody @Valid TraineeUpdateDto updateDto) {
        var trainee = traineeMapper.toTrainee(updateDto);
        var updatedTrainee = traineeService.update(trainee);
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
        var foundTrainee = traineeService.findByUsername(username);
        traineeService.delete(foundTrainee);
        return ResponseEntity.ok("Trainee with id=" + foundTrainee.getId() + " was deleted");
    }

    @Operation(
            summary = "Update trainee's trainer list",
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
    @PutMapping("/trainers")
    public ResponseEntity<List<TrainerDto>> updateTraineeTrainers(@RequestBody @Valid TraineeTrainerUpdateDto updateDto) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
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
    public ResponseEntity<List<TrainingShortView>> getTraineeTrainings(
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
                                TrainingType.valueOf(trainingType)
                        )
                        .stream()
                        .map(trainingMapper::toTrainingShortView)
                        .toList()
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
        var foundTrainee = traineeService.findByUsername(statusUpdateDto.getUserName());
        if (foundTrainee == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Trainee not found");
        }

        var isActive = statusUpdateDto.getIsActive();
        var currentStatus = isActive
                ? traineeService.activateStatus(foundTrainee.getId())
                : traineeService.deactivateStatus(foundTrainee.getId());

        return ResponseEntity.ok("Trainee with id=" + foundTrainee.getId() +
                (isActive ? " was activated." : " was deactivated.") +
                " Current status: " + currentStatus);
    }
}
