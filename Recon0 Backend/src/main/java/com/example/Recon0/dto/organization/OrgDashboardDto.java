package com.example.Recon0.dto.organization;

import com.example.Recon0.dto.reports.ReportDto;
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
public class OrgDashboardDto {
    private KpiDto kpis;
    private List<RecentReportDto> recentReports;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KpiDto {
        private long programCount;
        private long totalReports;
        private long newReports;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentReportDto {
        private String id;
        private String title;
        private String severity;
        private String status;
        private String createdAt;
    }
}
