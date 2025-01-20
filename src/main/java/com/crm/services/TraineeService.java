package com.crm.services;

import com.crm.repositories.entities.Trainee;

import java.time.LocalDate;

public interface TraineeService extends UserService<Trainee> {
    Trainee save(String firstName, String lastName, String password, String address, LocalDate dateOfBirth);

    void delete(Trainee trainee);

    void deleteByUsername(String username);
}
