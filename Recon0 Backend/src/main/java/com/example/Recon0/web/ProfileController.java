package com.example.Recon0.web;

import com.example.Recon0.dto.ApiResponse;
import com.example.Recon0.dto.ProfileDto;
import com.example.Recon0.dto.StatsDto;
import com.example.Recon0.dto.UpdateProfileRequest;
import com.example.Recon0.models.User;
import com.example.Recon0.services.ProfileService;
import com.example.Recon0.services.ReportMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1")
@Tag(name="User Profile Api", description = "Create and Get Profile, Stats ")
public class ProfileController {

    private final ProfileService profileService;


    public ProfileController(ProfileService profileService,ReportMessageService reportMessageService ) {
        this.profileService = profileService;

    }


    @GetMapping("/profile")
    @Operation(summary = "To get user profile info")
    public ResponseEntity<ApiResponse<ProfileDto>> getCurrentUserProfile() {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ProfileDto profile = profileService.getCurrentUserProfile(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(profile));
    }

    @PutMapping("/profile")
    @Operation(summary = "To update user profile info ")
    public ResponseEntity<ApiResponse<ProfileDto>> updateCurrentUserProfile(@Valid @RequestBody UpdateProfileRequest request) {
        try {
            User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            ProfileDto updatedProfile = profileService.updateCurrentUserProfile(currentUser.getId(),request);
            return ResponseEntity.ok(ApiResponse.success(updatedProfile));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }


    @GetMapping("/stats")
    @Operation(summary = "To get the statistic of the user")
    public ResponseEntity<ApiResponse<StatsDto>> getUserStats() {
        StatsDto stats = profileService.getUserStats();
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
}

