package com.example.Recon0.dto.reports;

import com.example.Recon0.models.ReportMessage;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("report_id")
    @JsonAlias({"report_id", "reportId"})
    private String reportId;

    @JsonProperty("sender_id")
    @JsonAlias({"sender_id", "senderId"})
    private String senderId;

    @JsonProperty("sender_username")
    @JsonAlias({"sender_username", "senderUsername"})
    private String senderUsername;

    @JsonProperty("sender_avatar_url")
    @JsonAlias({"sender_avatar_url", "senderAvatarUrl"})
    private String senderAvatarUrl;

    private String content;

    @JsonProperty("created_at")
    @JsonAlias({"created_at", "createdAt"})
    private OffsetDateTime createdAt;

    private List<AttachmentDto> attachments;

    /**
     * This is the static helper method that your service needs.
     * It takes a ReportMessage entity from the database and converts it
     * into a DTO suitable for sending as a JSON API response.
     */
    public static ReportMessageDto fromEntity(ReportMessage message) {
        String username = null;
        String avatarUrl = null;

        if (message.getSender() != null) {
            username = message.getSender().getDisplayName();
            if (username == null || username.isBlank()) {
                username = message.getSender().getFull_name();
            }
            if (username == null || username.isBlank()) {
                username = message.getSender().getEmail();
            }
            avatarUrl = message.getSender().getAvatar_url();
        }

        return ReportMessageDto.builder()
                .id(message.getId().toString())
                .reportId(message.getReport() != null ? message.getReport().getId().toString() : null)
                .senderId(message.getSender() != null ? message.getSender().getId().toString() : null)
                .senderUsername(username != null ? username : "Unknown User")
                .senderAvatarUrl(avatarUrl)
                .content(message.getContent())
                .createdAt(message.getCreatedAt())
                .attachments(message.getAttachments() != null ? message.getAttachments().stream()
                        .map(attachment -> new AttachmentDto(
                                attachment.getId().toString(),
                                attachment.getFileUrl(),
                                attachment.getFileName(),
                                attachment.getFileType(),
                                attachment.getUploadedAt() != null ? attachment.getUploadedAt().toString() : null
                        ))
                        .collect(Collectors.toList()) : List.of())
                .build();
    }
}

