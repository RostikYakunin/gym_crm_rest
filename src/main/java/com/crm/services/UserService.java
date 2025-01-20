package com.crm.services;

public interface UserService<T> {
    T findById(long id);

    T findByUsername(String username);

    T save(T entity);

    T update(T entity);

    boolean changePassword(T entity, String inputtedPassword, String newPassword);

    boolean activateStatus(long id);

    boolean deactivateStatus(long id);

    boolean isUsernameAndPasswordMatching(String username, String inputtedPassword);
}
