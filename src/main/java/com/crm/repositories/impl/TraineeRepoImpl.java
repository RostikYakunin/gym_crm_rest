package com.crm.repositories.impl;

import com.crm.models.TrainingType;
import com.crm.repositories.TraineeRepo;
import com.crm.repositories.entities.Trainee;
import com.crm.repositories.entities.Training;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@Slf4j
public class TraineeRepoImpl extends AbstractUserRepo<Trainee> implements TraineeRepo {
    @Override
    protected Class<Trainee> getEntityClass() {
        return Trainee.class;
    }

    @Override
    protected String getEntityClassName() {
        return Trainee.class.getSimpleName();
    }

    @Override
    public void delete(Trainee trainee) {
        log.debug("Start deleting entity...");
        if (!trainee.getTrainings().isEmpty()) {
            entityManager.createQuery("DELETE FROM Training t WHERE t.trainee.id = :traineeId")
                    .setParameter("traineeId", trainee.getId())
                    .executeUpdate();
        }

        entityManager.createQuery("DELETE FROM Trainee t WHERE t.id = :traineeId")
                .setParameter("traineeId", trainee.getId())
                .executeUpdate();
    }

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
