package com.crm;

import com.crm.config.AppConfig;
import com.crm.repositories.entities.Training;
import com.crm.services.TraineeService;
import com.crm.services.TrainerService;
import com.crm.services.TrainingService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class GymSpringApp {
    public static void main(String[] args) {
        var context = new AnnotationConfigApplicationContext(AppConfig.class);
    }
}
