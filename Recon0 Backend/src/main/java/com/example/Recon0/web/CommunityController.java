package com.example.Recon0.web;

import com.example.Recon0.dto.ApiResponse;
import com.example.Recon0.dto.community.AchievementDto;
import com.example.Recon0.dto.community.LeaderboardEntryDto;
import com.example.Recon0.dto.community.NotificationDto;
import com.example.Recon0.services.CommunityService;
import com.example.Recon0.services.NotificationService; // Import NotificationService
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Tag(name="Community API", description = "Leaderboard,Achievement,Notifiaction")
public class CommunityController {

    @Autowired
    private CommunityService communityService;

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/leaderboard")
    @Operation(summary = "To get the leaderboard")
    public ResponseEntity<ApiResponse<List<LeaderboardEntryDto>>> getLeaderboard() {
        List<LeaderboardEntryDto> leaderboard = communityService.getLeaderboard();
        return ResponseEntity.ok(ApiResponse.<List<LeaderboardEntryDto>>builder().success(true).data(leaderboard).build());
    }

    @GetMapping("/achievements")
    @Operation(summary = "To get all the achievement in the application")
    public ResponseEntity<ApiResponse<List<AchievementDto>>> getAllAchievements() {
        List<AchievementDto> achievements = communityService.getAllAchievements();
        return ResponseEntity.ok(ApiResponse.<List<AchievementDto>>builder().success(true).data(achievements).build());
    }

    @GetMapping("/achievements/my")
    @Operation(summary = "To get the user achievement")
    public ResponseEntity<ApiResponse<List<String>>> getMyAchievements() {
        List<String> myAchievementIds = communityService.getMyAchievements();
        return ResponseEntity.ok(ApiResponse.<List<String>>builder().success(true).data(myAchievementIds).build());
    }

    @GetMapping("/notifications")
    @Operation(summary = "To get the notification")
    public ResponseEntity<ApiResponse<List<NotificationDto>>> getNotifications() {
        List<NotificationDto> notifications = notificationService.getNotificationsForCurrentUser();
        return ResponseEntity.ok(ApiResponse.<List<NotificationDto>>builder().success(true).data(notifications).build());
    }
}
