package com.crm.repositories.impl;

import com.crm.enums.TrainingType;
import com.crm.repositories.CustomTraineeRepo;
import com.crm.repositories.entities.Training;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class CustomTraineeRepoImpl implements CustomTraineeRepo {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Training> getTraineeTrainingsByCriteria(String traineeUsername, LocalDate fromDate, LocalDate toDate, String trainerUserName, TrainingType trainingType) {
        var dynamicJpqlQuery = "SELECT t FROM Training t WHERE t.trainee.userName = :traineeUsername";

        if (fromDate != null) {
            dynamicJpqlQuery += " AND t.trainingDate >= :fromDate";
        }
        if (toDate != null) {
            dynamicJpqlQuery += " AND t.trainingDate <= :toDate";
        }
        if (trainerUserName != null && !trainerUserName.isEmpty()) {
            dynamicJpqlQuery += " AND t.trainer.userName = :trainerUserName";
        }
        if (trainingType != null) {
            dynamicJpqlQuery += " AND t.trainingType = :trainingType";
        }

        var query = entityManager.createQuery(dynamicJpqlQuery, Training.class);
        query.setParameter("traineeUsername", traineeUsername);

        if (fromDate != null) {
            query.setParameter("fromDate", fromDate);
        }
        if (toDate != null) {
            query.setParameter("toDate", toDate);
        }
        if (trainerUserName != null && !trainerUserName.isEmpty()) {
            query.setParameter("trainerUserName", trainerUserName);
        }
        if (trainingType != null) {
            query.setParameter("trainingType", trainingType);
        }

        return query.getResultList();
    }
}
