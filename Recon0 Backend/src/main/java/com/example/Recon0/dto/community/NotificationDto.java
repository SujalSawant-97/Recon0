package com.example.Recon0.dto.community;

import com.example.Recon0.models.Notification;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificationDto {
    private UUID id;

    @JsonProperty("user_id")
    @JsonAlias({"user_id", "userId"})
    private String userId;

    private String type;
    private String message;

    @JsonProperty("is_read")
    @JsonAlias({"is_read", "isRead"})
    private boolean isRead;

    @JsonProperty("created_at")
    @JsonAlias({"created_at", "createdAt"})
    private String createdAt;

    @JsonProperty("is_read")
    public boolean is_read() {
        return isRead;
    }

    @JsonProperty("created_at")
    public String getCreated_at() {
        return createdAt;
    }

    public static NotificationDto fromNotification(Notification notification) {
        return new NotificationDto(
                notification.getId(),
                notification.getUser() != null ? notification.getUser().getId().toString() : null,
                notification.getType(),
                notification.getMessage(),
                notification.isRead(),
                notification.getCreatedAt() != null ? notification.getCreatedAt().toString() : null
        );
    }
}
