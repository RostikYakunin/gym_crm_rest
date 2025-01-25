package com.crm.dtos.trainer;

import com.crm.models.TrainingType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrainerShortView {
    private String firstName;
    private String lastName;
    private String userName;
    private TrainingType specialization;
}