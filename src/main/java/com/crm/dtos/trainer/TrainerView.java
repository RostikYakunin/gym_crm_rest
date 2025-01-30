package com.crm.dtos.trainer;

import com.crm.dtos.training.TrainingView;
import com.crm.models.TrainingType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrainerView {
    private String firstName;
    private String lastName;
    private String userName;
    private TrainingType specialization;
    private Boolean isActive;

    @Builder.Default
    private Set<TrainingView> trainingViews = new HashSet<>();
}