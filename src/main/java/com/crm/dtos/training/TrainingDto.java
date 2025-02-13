package com.crm.dtos.training;

import com.crm.enums.TrainingType;
import com.crm.repositories.entities.Trainee;
import com.crm.repositories.entities.Trainer;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.time.DurationMax;
import org.hibernate.validator.constraints.time.DurationMin;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrainingDto {
    @NotNull(message = "Trainee is mandatory")
    private Trainee trainee;

    @NotNull(message = "Trainer is mandatory")
    private Trainer trainer;

    @NotBlank(message = "Training name is mandatory")
    @Size(min = 3, max = 255, message = "Training name must be between 3 and 255 characters")
    private String trainingName;

    @NotNull(message = "Training type is mandatory")
    private TrainingType trainingType;

    @NotNull(message = "Training date is mandatory")
    @Future(message = "Training date must be in the future")
    private LocalDateTime trainingDate;

    @NotNull(message = "Training duration is mandatory")
    @DurationMin(minutes = 20, message = "Min duration must be at least 20 minutes")
    @DurationMax(hours = 2, message = "Max duration must be less than 2 hours")
    private Duration trainingDuration;
}
