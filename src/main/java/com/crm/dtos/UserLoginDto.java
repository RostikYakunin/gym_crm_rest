package com.crm.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserLoginDto {
    @NotBlank(message = "User name is mandatory")
    @Size(min = 2, max = 255, message = "User name must be between 2 and 255 characters")
    private String userName;

    @NotBlank(message = "Old password is mandatory")
    @Size(min = 4, max = 10, message = "Password must be between 4 and 10 characters")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).*$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, and one number"
    )
    private String oldPassword;

    @NotBlank(message = "New password is mandatory")
    @Size(min = 4, max = 10, message = "Password must be between 4 and 10 characters")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).*$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, and one number"
    )
    private String newPassword;
}