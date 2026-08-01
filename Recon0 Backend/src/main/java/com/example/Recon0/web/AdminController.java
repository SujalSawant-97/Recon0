package com.example.Recon0.web;

import com.example.Recon0.dto.ApiResponse;
import com.example.Recon0.dto.admin.AdminUserDto;
import com.example.Recon0.dto.admin.PlatformAnalyticsDto;
import com.example.Recon0.dto.admin.UpdateUserStatusRequest;
import com.example.Recon0.models.User;
import com.example.Recon0.services.AdminService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')") // Secures all endpoints in this controller
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<AdminUserDto>>> getAllUsers() {
        List<AdminUserDto> users = adminService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.<List<AdminUserDto>>builder().success(true).data(users).build());
    }

    @PatchMapping("/users/{userId}/status")
    public ResponseEntity<ApiResponse<AdminUserDto>> updateUserStatus(@PathVariable UUID userId, @Valid @RequestBody UpdateUserStatusRequest request) {
        User updatedUser = adminService.updateUserStatus(userId, request);
        return ResponseEntity.ok(ApiResponse.<AdminUserDto>builder().success(true).data(AdminUserDto.fromEntity(updatedUser)).build());
    }

    @GetMapping("/analytics")
    public ResponseEntity<ApiResponse<PlatformAnalyticsDto>> getPlatformAnalytics() {
        PlatformAnalyticsDto analytics = adminService.getPlatformAnalytics();
        return ResponseEntity.ok(ApiResponse.<PlatformAnalyticsDto>builder().success(true).data(analytics).build());
    }
}
