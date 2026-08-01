package com.example.Recon0.dto.admin;

import com.example.Recon0.models.User;
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

    public static AdminUserDto fromEntity(User user) {
        return AdminUserDto.builder()
                .id(user.getId().toString())
                .username(user.getUsername())
                .fullName(user.getFull_name())
                .email(user.getEmail())
                .role(user.getRole())
                .status(user.getStatus())
                .reputationPoints(user.getReputationPoints())
                .createdAt(user.getCreated_at().toString())
                .build();
    }
}
