package com.crm.repositories.impl;

import com.crm.enums.TrainingType;
import com.crm.repositories.CustomTrainerRepo;
import com.crm.repositories.entities.Training;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class CustomTrainerRepoImpl implements CustomTrainerRepo {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Training> getTrainerTrainingsByCriteria(
            String trainerUsername,
            LocalDate fromDate,
            LocalDate toDate,
            String traineeUserName,
            TrainingType trainingType
    ) {
        var dynamicJpqlQuery = "SELECT t FROM Training t WHERE t.trainer.userName = :trainerUsername";

        if (fromDate != null) {
            dynamicJpqlQuery += " AND t.trainingDate >= :fromDate";
        }
        if (toDate != null) {
            dynamicJpqlQuery += " AND t.trainingDate <= :toDate";
        }
        if (traineeUserName != null && !traineeUserName.isEmpty()) {
            dynamicJpqlQuery += " AND (t.trainee.firstName LIKE :traineeName OR t.trainee.lastName LIKE :traineeName)";
        }
        if (trainingType != null) {
            dynamicJpqlQuery += " AND t.trainingType = :trainingType";
        }

        var query = entityManager.createQuery(dynamicJpqlQuery, Training.class);
        query.setParameter("trainerUsername", trainerUsername);

        if (fromDate != null) {
            query.setParameter("fromDate", fromDate);
        }
        if (toDate != null) {
            query.setParameter("toDate", toDate);
        }
        if (traineeUserName != null && !traineeUserName.isEmpty()) {
            query.setParameter("traineeName", "%" + traineeUserName + "%");
        }
        if (trainingType != null) {
            query.setParameter("trainingType", trainingType);
        }

        return query.getResultList();
    }
}
