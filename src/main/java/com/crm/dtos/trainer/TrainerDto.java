package com.crm.dtos.trainer;

import com.crm.dtos.UserDto;
import com.crm.models.TrainingType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrainerDto extends UserDto {
    @NotNull(message = "Specialization is mandatory")
    private TrainingType specialization;
}
