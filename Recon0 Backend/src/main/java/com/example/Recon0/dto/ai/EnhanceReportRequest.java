package com.example.Recon0.dto.ai;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class EnhanceReportRequest {
    private String description;

    @JsonProperty("steps_to_reproduce")
    @JsonAlias({"steps_to_reproduce", "stepsToReproduce"})
    private String stepsToReproduce;

    private String impact;
}
