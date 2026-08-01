package com.example.Recon0.web;

import com.example.Recon0.dto.ApiResponse;
import com.example.Recon0.dto.reports.*;
import com.example.Recon0.services.ReportMessageService;
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
@RequestMapping("/api/v1")
@Tag(name="Report Api", description = "Accessed By user for report and message")
public class ReportController {

    private final ReportService reportService;
    private final ReportMessageService reportMessageService;

    public ReportController(ReportService reportService,ReportMessageService reportMessageService) {
        this.reportService = reportService;
        this.reportMessageService=reportMessageService;
    }


    @PostMapping("/reports")
    @Operation(summary = "To create the report")
    public ResponseEntity<ApiResponse<ReportDto>> submitReport(@Valid @RequestBody SubmitReportRequest request) {
        // Add @PreAuthorize("hasRole('hacker')") for security
        try {
            System.out.println(request);
            ReportDto createdReport = reportService.submitReport(request);
            return new ResponseEntity<>(ApiResponse.success(createdReport), HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // The API contract has /my-reports, so we create a separate controller/endpoint for that
    // to avoid ambiguity with /reports/{id}
    @GetMapping("/my-reports")
    @Operation(summary = "To get the user reports")
    public ResponseEntity<ApiResponse<List<ReportDto>>> getMyReports() {
        // Add @PreAuthorize("hasRole('hacker')") for security
        List<ReportDto> reports = reportService.getMyReports();
        return ResponseEntity.ok(ApiResponse.success(reports));
    }
    @GetMapping("/reports/{reportId}/messages")
    @Operation(summary = "To get the message in report")
    public ResponseEntity<ApiResponse<List<ReportMessageDto>>> getReportMessages(@PathVariable("reportId") UUID reportId) {
        List<ReportMessageDto> messages = reportMessageService.getMessagesForReport(reportId);
        ApiResponse<List<ReportMessageDto>> response = ApiResponse.<List<ReportMessageDto>>builder()
                .success(true)
                .data(messages)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reports/{reportId}/messages")
    @Operation(summary = "To send the message in report")
    public ResponseEntity<ApiResponse<ReportMessageDto>> sendReportMessage(
            @PathVariable("reportId") UUID reportId,
            @RequestBody SendMessageRequest request) {
        ReportMessageDto newMessage = reportMessageService.sendMessage(reportId, request);
        ApiResponse<ReportMessageDto> response = ApiResponse.<ReportMessageDto>builder()
                .success(true)
                .data(newMessage)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/reports/{id}")
    @Operation(summary = "To get user report details")
    public ResponseEntity<ApiResponse<ReportDetailDto>> getReportById(@PathVariable("id") UUID id) {
        ReportDetailDto reportDetails = reportService.getReportDetails(id);
        ApiResponse<ReportDetailDto> response = ApiResponse.<ReportDetailDto>builder()
                .success(true)
                .data(reportDetails)
                .build();
        return ResponseEntity.ok(response);
    }
}
