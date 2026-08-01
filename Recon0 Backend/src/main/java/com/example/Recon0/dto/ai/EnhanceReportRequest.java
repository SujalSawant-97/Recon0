package com.example.Recon0.dto.ai;

import lombok.Data;

@Data
public class EnhanceReportRequest {
    private String description;
    private String stepsToReproduce;
    private String impact;
}
