package com.example.Recon0.dto.ai;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class AiChatRequest {
    @NotEmpty(message = "Question must not be empty")
    private String question;
}
