package com.example.Recon0.dto.admin;

import lombok.Builder;
import lombok.Data;
import java.util.Map;

@Data
@Builder
public class PlatformAnalyticsDto {
    private Kpis kpis;
    private Map<String, Long> reportsByStatus;

    @Data
    @Builder
    public static class Kpis {
        private long totalUsers;
        private long totalHackers;
        private long totalOrgs;
        private long totalReports;
        private long totalPrograms;
    }
}
