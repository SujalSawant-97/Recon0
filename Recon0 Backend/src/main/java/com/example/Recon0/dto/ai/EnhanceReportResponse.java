package com.example.Recon0.dto.ai;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EnhanceReportResponse {
    private String description;

    @JsonProperty("steps_to_reproduce")
    @JsonAlias({"steps_to_reproduce", "stepsToReproduce"})
    private String stepsToReproduce;

    private String impact;

    @JsonProperty("stepsToReproduce")
    public String getStepsToReproduce() {
        return stepsToReproduce;
    }

    @JsonProperty("steps_to_reproduce")
    public String getSteps_to_reproduce() {
        return stepsToReproduce;
    }
}
