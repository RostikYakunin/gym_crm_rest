package com.crm.repositories;

import com.crm.repositories.entities.User;

import java.util.Optional;

public interface UserRepo<T extends User> extends BaseRepo<T> {
    Optional<T> findByUserName(String username);

    boolean isUserNameExists(String username);

    boolean existsByFirstNameAndLastName(String firstName, String lastName);
}
