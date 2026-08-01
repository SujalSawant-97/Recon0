package com.example.Recon0.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnhanceReportResponse {
    private String description;
    private String stepsToReproduce;
    private String impact;
}
