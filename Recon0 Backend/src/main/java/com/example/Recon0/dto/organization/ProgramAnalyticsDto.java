package com.example.Recon0.dto.organization;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgramAnalyticsDto {
    private String programTitle;
    private KpiDto kpis;
    private List<Map<String, Object>> reportsBySeverity;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KpiDto {
        private long totalReports;
        private long resolvedReports;
        // avgTimeToBountyDays and totalPaidOut are complex and will be mocked for now
        private int avgTimeToBountyDays;
        private int totalPaidOut;
    }
}