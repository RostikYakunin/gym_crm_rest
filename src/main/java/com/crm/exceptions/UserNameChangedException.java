package com.crm.exceptions;

public class UserNameChangedException extends RuntimeException {
    public UserNameChangedException(String message) {
        super(message);
    }

    public UserNameChangedException() {
        this("User name changing is forbidden!");
    }
}