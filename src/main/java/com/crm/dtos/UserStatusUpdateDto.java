package com.crm.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserStatusUpdateDto {
    @NotBlank(message = "User name is mandatory")
    @Size(min = 2, max = 255, message = "Last name must be between 2 and 255 characters")
    private String userName;

    @NotNull(message = "isActive must not be null")
    private Boolean isActive;
}