package com.example.Recon0.web;

import com.example.Recon0.dto.ApiResponse;
import com.example.Recon0.dto.ProgramDto;
import com.example.Recon0.dto.organization.CreateProgramRequest;
import com.example.Recon0.dto.organization.OrgDashboardDto;
import com.example.Recon0.dto.organization.ProgramAnalyticsDto;
import com.example.Recon0.dto.organization.UpdateReportStatusRequest;
import com.example.Recon0.dto.reports.ReportDetailDto;
import com.example.Recon0.dto.reports.ReportDto;
import com.example.Recon0.services.OrganizationService;
import com.example.Recon0.services.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/organization")
@Tag(name="Organization Api", description = "Dashboard, Analytics, Reports, Program")
// Add @PreAuthorize("hasRole('organization')") at the class level
public class OrganizationController {

    private final OrganizationService organizationService;
    private final  ReportService reportService;

    public OrganizationController(OrganizationService organizationService, ReportService reportService) {
        this.organizationService = organizationService;
        this.reportService =reportService;
    }

    @GetMapping("/dashboard")
    @Operation(summary = "To get organization dashboard")
    public ResponseEntity<ApiResponse<OrgDashboardDto>> getDashboard() {
        OrgDashboardDto dashboardData = organizationService.getOrganizationDashboard();
        return ResponseEntity.ok(ApiResponse.success(dashboardData));
    }
    @GetMapping("/my-programs")
    @Operation(summary = "To get program related to organization user")
    public ResponseEntity<ApiResponse<List<ProgramDto>>> getMyPrograms() {
        List<ProgramDto> programs = organizationService.getMyPrograms();
        ApiResponse<List<ProgramDto>> response = ApiResponse.<List<ProgramDto>>builder()
                .success(true)
                .data(programs)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/my-programs")
    @Operation(summary = "To create the program for the organization")
    public ResponseEntity<ApiResponse<ProgramDto>> createProgram(@Valid @RequestBody CreateProgramRequest request) {
        ProgramDto createdProgram = organizationService.createProgram(request);
        return new ResponseEntity<>(ApiResponse.success(createdProgram), HttpStatus.CREATED);
    }

    @PatchMapping("/reports/{reportId}")
    @Operation(summary = "To update report status")
    public ResponseEntity<ApiResponse<ReportDto>> updateReportStatus(@PathVariable UUID reportId, @Valid @RequestBody UpdateReportStatusRequest request) {
        try {
            ReportDto updatedReport = organizationService.updateReportStatus(reportId, request);
            return ResponseEntity.ok(ApiResponse.success(updatedReport));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }

    }
    @GetMapping("/reports")
    @Operation(summary = "To get reports for the organization")
    public ResponseEntity<ApiResponse<List<ReportDetailDto>>> getReport() {
        try {
            List<ReportDetailDto> reportDetails = reportService.getReport();
            ApiResponse<List<ReportDetailDto>> response = ApiResponse.<List<ReportDetailDto>>builder()
                    .success(true)
                    .data(reportDetails)
                    .build();
            return ResponseEntity.ok(response);
        }catch(RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    @GetMapping("/programs/{programId}/analytics")
    @Operation(summary = "To analyse the program")
    public ResponseEntity<ApiResponse<ProgramAnalyticsDto>> getProgramAnalytics(@PathVariable UUID programId) {
        try{
            ProgramAnalyticsDto analytics = organizationService.getProgramAnalytics(programId);

        ApiResponse<ProgramAnalyticsDto> response = ApiResponse.<ProgramAnalyticsDto>builder()
                .success(true)
                .data(analytics)
                .build();
        return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
