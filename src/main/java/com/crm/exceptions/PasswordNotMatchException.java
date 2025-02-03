package com.crm.exceptions;


public class PasswordNotMatchException extends RuntimeException {
    public PasswordNotMatchException(String message) {
        super(message);
    }

    public PasswordNotMatchException() {
        this("Inputted password does not match with password from DB!");
    }
}