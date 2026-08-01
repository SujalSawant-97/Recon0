package com.example.Recon0.dto.admin;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class UpdateUserStatusRequest {
    @NotEmpty(message = "Status must not be empty")
    private String status;
}
