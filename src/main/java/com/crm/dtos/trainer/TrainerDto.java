package com.crm.dtos.trainer;

import com.crm.dtos.UserDto;
import com.crm.models.TrainingType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class TrainerDto extends UserDto {
    @NotNull(message = "Specialization is mandatory")
    private TrainingType specialization;
}
