package com.crm.dtos.training;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class TrainingTypeView {
    private final int id;
    private final String name;
}