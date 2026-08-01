package com.example.Recon0.services;

import com.example.Recon0.dto.reports.ReportMessageDto;
import com.example.Recon0.dto.reports.SendMessageRequest;
import com.example.Recon0.models.*;
import com.example.Recon0.repositories.ReportMessageRepository;
import com.example.Recon0.repositories.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReportMessageService {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private ReportMessageRepository reportMessageRepository;

    @Autowired
    private NotificationService notificationService;

    @Transactional(readOnly = true)
    public List<ReportMessageDto> getMessagesForReport(UUID reportId) {
        Report report = findReportAndCheckAccess(reportId);
        List<ReportMessage> messages = reportMessageRepository.findByReportOrderByCreatedAtAsc(report);
        return messages.stream()
                .map(ReportMessageDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReportMessageDto sendMessage(UUID reportId, SendMessageRequest request) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Report report = findReportAndCheckAccess(reportId);

        ReportMessage message = ReportMessage.builder()
                .report(report)
                .sender(currentUser)
                .content(request.getContent())
                .build();

        if (request.getAttachments() != null && !request.getAttachments().isEmpty()) {
            List<MessageAttachment> attachments = request.getAttachments().stream()
                    .map(dto -> MessageAttachment.builder()
                            .message(message)
                            .fileUrl(dto.getUrl())
                            .fileName(dto.getName())
                            .fileType(dto.getType())
                            .build())
                    .collect(Collectors.toList());
            message.getAttachments().addAll(attachments);
        }

        ReportMessage savedMessage = reportMessageRepository.save(message);

        // Notify the other party
        User recipient = report.getReporter().getId().equals(currentUser.getId())
                ? report.getProgram().getOrganization_id()
                : report.getReporter();
        String notificationMessage = String.format("You have a new message on report: '%s'", report.getTitle());
        notificationService.createNotification(recipient, "report_message", notificationMessage);

        return ReportMessageDto.fromEntity(savedMessage);
    }

    private Report findReportAndCheckAccess(UUID reportId) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found with id: " + reportId));

        boolean isReporter = report.getReporter().getId().equals(currentUser.getId());
        boolean isProgramOwner = report.getProgram().getOrganization_id().getId().equals(currentUser.getId());

        if (!isReporter && !isProgramOwner) {
            throw new AccessDeniedException("You do not have permission to access messages for this report.");
        }
        return report;
    }
}
