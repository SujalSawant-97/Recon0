package com.example.Recon0.services;

import com.example.Recon0.dto.ProfileDto;
import com.example.Recon0.dto.StatsDto;
import com.example.Recon0.dto.UpdateProfileRequest;
import com.example.Recon0.models.User;
import com.example.Recon0.repositories.ReportRepository;
import com.example.Recon0.repositories.UserRepository;

import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ProfileService {

    private final UserRepository userRepository;
    private final ReportRepository reportRepository;


    public ProfileService(UserRepository userRepository, ReportRepository reportRepository) {
        this.userRepository = userRepository;
        this.reportRepository = reportRepository;
    }
    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "profiles", key =  "#userId")
    public ProfileDto getCurrentUserProfile(UUID userId) {
        User user = getCurrentUser();
        return ProfileDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getDisplayName())
                .full_name(user.getFull_name())
                .role(user.getRole())
                .status(user.getStatus())
                .reputation_points(user.getReputationPoints())
                .avatar_url(user.getAvatar_url())
                .bio(user.getBio())
                .created_at(user.getCreated_at())
                .build();
    }


    @Transactional
    @CacheEvict(value = "profiles", key =  "#userId")
    public ProfileDto updateCurrentUserProfile(UUID userId, UpdateProfileRequest request) {
        User user = getCurrentUser();
        user.setFull_name(request.getFull_name());
        user.setUsername(request.getUsername());
        user.setBio(request.getBio());
        User updatedUser = userRepository.save(user);
        return getCurrentUserProfile(userId); // Re-fetch to build DTO
    }

    public StatsDto getUserStats() {
        User user = getCurrentUser();

        long reportsSubmitted = reportRepository.countByReporter(user);
        long reportsAccepted = reportRepository.countByReporterAndStatus(user, "Accepted");
        Integer bountiesEarned = reportRepository.sumBountiesForAcceptedReports(user.getId());

        return StatsDto.builder()
                .reputation_points(user.getReputationPoints())
                .reports_submitted((int) reportsSubmitted)
                .reports_accepted((int) reportsAccepted)
                .bounties_earned(bountiesEarned != null ? bountiesEarned : 0)
                .build();
    }
}
