package com.crm.repositories.impl;

import com.crm.repositories.TrainingRepo;
import com.crm.repositories.entities.Training;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Slf4j
public class TrainingRepoImpl implements TrainingRepo {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Training> findById(long id) {
        log.debug("Start searching training by id... ");
        return Optional.ofNullable(entityManager.find(Training.class, id));
    }

    @Override
    public Training save(Training entity) {
        log.debug("Start saving new training... ");
        entityManager.persist(entity);
        return entity;
    }

    @Override
    public Training update(Training entity) {
        log.debug("Start updating training with id= " + entity.getId());
        return entityManager.merge(entity);
    }

    @Override
    public void delete(Training entity) {
        log.debug("Start deleting training with id= " + entity.getId());
        entityManager.remove(entity);
    }

    @Override
    public boolean isExistsById(long id) {
        log.debug("Start searching training with id= " + id);
        return entityManager.find(Training.class, id) != null;
    }
}
