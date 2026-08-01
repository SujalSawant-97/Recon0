package com.example.Recon0.services;

import com.example.Recon0.dto.reports.ReportDetailDto;
import com.example.Recon0.dto.reports.ReportDto;
import com.example.Recon0.dto.reports.SubmitReportRequest;
import com.example.Recon0.models.*;
import com.example.Recon0.repositories.ProgramRepository;
import com.example.Recon0.repositories.ReportAttachmentRepository;
import com.example.Recon0.repositories.ReportRepository;
import com.example.Recon0.repositories.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final ProgramRepository programRepository;
    private final ReportAttachmentRepository reportAttachmentRepository;

    public ReportService(ReportRepository reportRepository, UserRepository userRepository, ProgramRepository programRepository, ReportAttachmentRepository reportAttachmentRepository) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.programRepository = programRepository;
        this.reportAttachmentRepository = reportAttachmentRepository;
    }

    private User getCurrentUser() {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(currentUser)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Transactional
    public ReportDetailDto submitReport(SubmitReportRequest request) {
        User currentUser = getCurrentUser();
        Program program = programRepository.findById(request.getProgramId())
                .orElseThrow(() -> new RuntimeException("Program not found"));

        Report report = new Report();
        report.setReporter(getCurrentUser());
        report.setProgram(program);
        report.setProgramId(request.getProgramId());
        report.setTitle(request.getTitle());
        report.setSeverity(request.getSeverity());
        report.setDescription(request.getDescription());
        report.setSteps_to_reproduce(request.getSteps_to_reproduce());
        report.setStatus("New");
        report.setImpact(request.getImpact());

        // Save the main report first to get its ID
        Report savedReport = reportRepository.save(report);

        // Handle attachments
        List<ReportAttachment> attachments = new ArrayList<>();
        if (request.getAttachments() != null && !request.getAttachments().isEmpty()) {
            for (var attDto : request.getAttachments()) {
                ReportAttachment attachment = new ReportAttachment();
                attachment.setReport(savedReport);
                attachment.setFile_url(attDto.getUrl());
                attachment.setFile_name(attDto.getName());
                attachment.setFile_type(attDto.getType());
                attachments.add(attachment);
            }
            reportAttachmentRepository.saveAll(attachments);
        }
        savedReport.setAttachments(attachments);

        return ReportDetailDto.fromReport(savedReport);
    }

    @Transactional(readOnly = true)
    public List<ReportDto> getMyReports() {
        User currentUser = getCurrentUser();
        //User org =programRepository.findByOrganization(currentUser);
        return reportRepository.findByReporter(currentUser).stream()
                .map(ReportDto::fromReport)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ReportDetailDto getReportDetails(UUID reportId) {
        User currentUser = getCurrentUser();
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        // Security Check: Ensure the person fetching is either the reporter or part of the program's organization
        // This is a simplified check. A real implementation would be more robust.
        boolean isReporter = report.getReporter().getId().equals(currentUser.getId());
        boolean isOrgMember = report.getProgram().getOrganization_id().equals(currentUser);

        if (!isReporter  && !isOrgMember ) {
          throw new SecurityException("You do not have permission to view this report.");
         }

        return ReportDetailDto.fromReport(report);
    }
    @Transactional(readOnly = true)
    public List<ReportDetailDto> getReport() {
        User currentUser = getCurrentUser();
        List<Program> prg = programRepository.findByOrganization(currentUser);
        if (prg.isEmpty()) {
            return Collections.emptyList();
        }
        //List<Report> report =
        List<Report> reports = reportRepository.findByProgramInOrderByCreatedAtDesc(prg);

        return reports.stream()
                .map(ReportDetailDto::fromReport)
                .collect(Collectors.toList());
                //.orElseThrow(() -> new RuntimeException("Report not found"));

        // Security Check: Ensure the person fetching is either the reporter or part of the program's organization
        // This is a simplified check. A real implementation would be more robust.
//        boolean isReporter = report.getReporter().getId().equals(currentUser.getId());
//        boolean isOrgMember = report.getProgram().getOrganization_id().equals(currentUser);

//        if (!isReporter  && !isOrgMember ) {
//            throw new SecurityException("You do not have permission to view this report.");
//        }

        //return ReportDetailDto.fromReport(report);
    }
}
