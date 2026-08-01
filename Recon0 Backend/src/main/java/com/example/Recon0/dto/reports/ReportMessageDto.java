package com.example.Recon0.dto.reports;

import com.example.Recon0.models.ReportMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportMessageDto {
    private String id;
    private String reportId;
    private String senderId;
    private String content;
    private OffsetDateTime createdAt;
    private List<AttachmentDto> attachments;

    /**
     * This is the static helper method that your service needs.
     * It takes a ReportMessage entity from the database and converts it
     * into a DTO suitable for sending as a JSON API response.
     */
    public static ReportMessageDto fromEntity(ReportMessage message) {
        return ReportMessageDto.builder()
                .id(message.getId().toString())
                .reportId(message.getReport().getId().toString())
                .senderId(message.getSender().getId().toString())
                .content(message.getContent())
                .createdAt(message.getCreatedAt())
                .attachments(message.getAttachments().stream()
                        .map(attachment -> new AttachmentDto(
                                attachment.getId().toString(),
                                attachment.getFileUrl(),
                                attachment.getFileName(),
                                attachment.getFileType(),
                                attachment.getUploadedAt().toString()
                        ))
                        .collect(Collectors.toList()))
                .build();
    }
}

