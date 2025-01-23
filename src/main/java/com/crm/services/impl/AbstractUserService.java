package com.crm.services.impl;

import com.crm.repositories.UserRepo;
import com.crm.repositories.entities.User;
import com.crm.services.UserService;
import com.crm.utils.UserUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.NoSuchElementException;

@Slf4j
@RequiredArgsConstructor
@Transactional
public abstract class AbstractUserService<T extends User, R extends UserRepo<T>> implements UserService<T> {
    protected final R repository;

    @Override
    public T findById(long id) {
        log.info("Searching for entity with id={}", id);
        return repository.findById(id).orElse(null);
    }

    @Override
    public T findByUsername(String username) {
        log.info("Searching for entity with username={}", username);
        return repository.findByUserName(username).orElse(null);
    }

    @Override
    public T save(T entity) {
        log.info("Checking if user already registered in the system.");
        var isExists = repository.existsByFirstNameAndLastName(
                entity.getFirstName(),
                entity.getLastName()
        );

        if (isExists) {
            throw new IllegalStateException("User is already registered in the system.");
        }

        log.info("Starting saving entity with first name: {}", entity.getFirstName());
        var uniqueUsername = UserUtils.generateUniqueUsername(
                entity,
                repository::isUserNameExists
        );

        entity.setUserName(uniqueUsername);
        entity.setPassword(UserUtils.hashPassword(entity.getPassword()));
        entity.setActive(true);

        var savedTrainer = repository.save(entity);
        log.info("Entity with id={} was successfully saved", savedTrainer.getId());

        return savedTrainer;
    }

    @Override
    public T update(T entity) {
        var id = entity.getId();
        log.info("Started updating process for entity with id={}", id);

        if (!repository.isExistsById(id)) {
            log.error("Entity with id={} not found, update failed", id);
            throw new NoSuchElementException("Entity with id=" + id + " not found");
        }

        log.info("Starting updating entity...");
        var updatedEntity = repository.update(entity);
        log.info("Entity with id={} was successfully updated", id);

        return updatedEntity;
    }

    @Override
    public boolean changePassword(T entity, String inputtedPassword, String newPassword) {
        var result = UserUtils.matchesPasswordHash(inputtedPassword, entity.getPassword());
        if (!result) {
            log.error("Inputted password does not match password from DB");
            return false;
        }

        log.info("Changing password for entity...");
        entity.setPassword(UserUtils.hashPassword(newPassword));
        repository.update(entity);

        log.info("Password change successfully completed");
        return true;
    }

    @Override
    public boolean activateStatus(long id) {
        log.info("Activating status for entity with id={}", id);

        var foundEntity = repository.findById(id);
        if (foundEntity.isPresent()) {
            var entity = foundEntity.get();
            entity.setActive(true);
            return repository.update(entity).isActive();
        }

        return false;
    }

    @Override
    public boolean deactivateStatus(long id) {
        log.info("Deactivating status for entity with id={}", id);

        var foundEntity = repository.findById(id);
        if (foundEntity.isPresent()) {
            var entity = foundEntity.get();
            entity.setActive(false);
            return repository.update(entity).isActive();
        }

        return false;
    }

    @Override
    public boolean isUsernameAndPasswordMatching(String username, String inputtedPassword) {
        return repository.findByUserName(username)
                .map(user -> UserUtils.matchesPasswordHash(inputtedPassword, user.getPassword()))
                .orElse(false);
    }
}
