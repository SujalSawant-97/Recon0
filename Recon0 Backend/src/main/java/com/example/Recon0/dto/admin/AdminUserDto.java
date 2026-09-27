package com.example.Recon0.dto.admin;

import com.example.Recon0.models.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminUserDto {
    private String id;
    private String username;
    private String fullName;
    private String email;
    private String role;
    private String status;
    private int reputationPoints;
    private String createdAt;

    @JsonProperty("avatar_url")
    private String avatar_url;

    public static AdminUserDto fromEntity(User user) {
        return AdminUserDto.builder()
                .id(user.getId() != null ? user.getId().toString() : null)
                .username(user.getDisplayName())
                .fullName(user.getFull_name())
                .email(user.getEmail())
                .role(user.getRole())
                .status(user.getStatus())
                .reputationPoints(user.getReputationPoints())
                .createdAt(user.getCreated_at() != null ? user.getCreated_at().toString() : null)
                .avatar_url(user.getAvatar_url())
                .build();
    }
}
