package com.crm.dtos.training;

import com.crm.models.TrainingType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class TrainingShortView {
    private String name;
    private LocalDateTime date;
    private TrainingType type;
    private Duration duration;
    private String traineeUserName;
}