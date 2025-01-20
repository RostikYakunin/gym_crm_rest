package com.crm.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@AllArgsConstructor
@Slf4j
public enum TrainingType {
    FITNESS("Fitness"),
    YOGA("Yoga"),
    ZUMBA("Zumba"),
    STRETCHING("Stretching"),
    RESISTANCE("Resistance");

    private final String name;
}
