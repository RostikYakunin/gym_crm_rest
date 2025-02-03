package com.crm.services;

import com.crm.dtos.UserLoginDto;
import com.crm.repositories.entities.User;

public interface UserService<T extends User> {
    T findById(long id);

    T findByUsername(String username);

    T findByUsernameOrThrow(String userName);

    T save(T entity);

    T update(T entity);

    void changePassword(UserLoginDto loginDto);

    boolean activateStatus(long id);

    boolean deactivateStatus(long id);

    boolean isUsernameAndPasswordMatching(String username, String inputtedPassword);
}
