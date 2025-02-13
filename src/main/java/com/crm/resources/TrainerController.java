package com.crm.resources;

import com.crm.dtos.UserLoginDto;
import com.crm.dtos.UserStatusUpdateDto;
import com.crm.dtos.trainer.TrainerDto;
import com.crm.dtos.trainer.TrainerView;
import com.crm.dtos.training.TrainingView;
import com.crm.enums.TrainingType;
import com.crm.services.TrainerService;
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
@RequestMapping("/api/v1/trainer")
@RequiredArgsConstructor
@Tag(name = "REST API for Trainer", description = "Provides resource methods for managing trainers")
public class TrainerController {
    private final TrainerService trainerService;

    @Operation(
            summary = "Register a new trainer",
            description = "Creates a new trainer account and returns credentials.",
            parameters = {
                    @Parameter(name = "trainerDto", description = "TrainerDto object", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "201", description = "Trainer created"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @PostMapping
    public ResponseEntity<TrainerDto> registerTrainer(@RequestBody @Valid TrainerDto trainerDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(trainerService.addTrainer(trainerDto));
    }

    @Operation(
            summary = "Change trainer`s password",
            description = "Updates the password for a given username.",
            parameters = {
                    @Parameter(name = "loginDto", description = "UserLoginDto object", required = true),
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Login successful"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @PutMapping("/password")
    public ResponseEntity<String> changePassword(@RequestBody @Valid UserLoginDto loginDto) {
        trainerService.changePassword(loginDto);
        return ResponseEntity.ok("Password successfully changed");
    }

    @Operation(
            summary = "Get trainer profile",
            description = "Retrieves trainer details by username.",
            parameters = {
                    @Parameter(name = "username", description = "Trainer`s username.", required = true)
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
    public ResponseEntity<TrainerView> findTrainerProfile(@PathVariable("username") String username) {
        return ResponseEntity.ok(trainerService.findProfileByUserName(username));
    }

    @Operation(
            summary = "Update trainer`s profile",
            description = "Modifies trainer details.",
            parameters = {
                    @Parameter(name = "updateDto", description = "TrainerUpdateDto object.", required = true)
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
    public ResponseEntity<TrainerView> updateTrainer(
            @PathVariable("id") Long id,
            @RequestBody @Valid TrainerDto updateDto
    ) {
        return ResponseEntity.ok(trainerService.updateTrainerProfile(id, updateDto));
    }

    @Operation(
            summary = "Get not assigned active trainers for a trainee",
            parameters = {
                    @Parameter(name = "username", description = "Trainee`s username", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "List was found successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @GetMapping("/unassigned/{username}")
    public ResponseEntity<Set<TrainerDto>> findNotAssignedTrainers(@PathVariable("username") String username) {
        return ResponseEntity.ok(trainerService.findNotAssignedTrainersByTraineeUserName(username));
    }

    @Operation(
            summary = "Get trainer trainings list",
            parameters = {
                    @Parameter(name = "username", description = "Trainer`s username", required = true),
                    @Parameter(name = "periodFrom", description = "Criteria - period from."),
                    @Parameter(name = "periodTo", description = "Criteria - period to."),
                    @Parameter(name = "traineeName", description = "Trainee`s username")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "List was found successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @GetMapping("/trainings")
    public ResponseEntity<Set<TrainingView>> getTrainerTrainings(
            @RequestParam("username") String username,
            @RequestParam(value = "period-from", required = false) LocalDate periodFrom,
            @RequestParam(value = "period-to", required = false) LocalDate periodTo,
            @RequestParam(value = "trainee-username", required = false) String traineeUserName,
            @RequestParam(name = "training-type", required = false) String trainingType
    ) {
        return ResponseEntity.ok(
                trainerService.findTrainerTrainingsByCriteria(
                        username,
                        periodFrom,
                        periodTo,
                        traineeUserName,
                        Optional.ofNullable(trainingType).map(TrainingType::valueOf).orElse(null)
                )
        );
    }

    @Operation(
            summary = "Activate/De-Activate Trainer",
            description = "Toggle current trainer`s status to chosen.",
            parameters = {
                    @Parameter(name = "statusUpdateDto", description = "UserStatusUpdateDto object", required = true),
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Trainer profile deleted successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @PatchMapping("/status")
    public ResponseEntity<String> updateTraineeStatus(@RequestBody @Valid UserStatusUpdateDto statusUpdateDto) {
        var foundTrainer = trainerService.findByUsernameOrThrow(statusUpdateDto.getUserName());
        var isActive = statusUpdateDto.getIsActive()
                ? trainerService.activateStatus(foundTrainer.getId())
                : trainerService.deactivateStatus(foundTrainer.getId());

        return ResponseEntity.ok(
                "Trainer with userName=" + foundTrainer.getUserName() +
                        (isActive ? " was activated." : " was deactivated.")
        );
    }
}
