package com.example.Recon0.services;

import com.example.Recon0.dto.admin.AdminUserDto;
import com.example.Recon0.dto.admin.PlatformAnalyticsDto;
import com.example.Recon0.dto.admin.UpdateUserStatusRequest;
import com.example.Recon0.models.User;
import com.example.Recon0.models.Report;
import com.example.Recon0.repositories.ProgramRepository;
import com.example.Recon0.repositories.ReportRepository;
import com.example.Recon0.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private ProgramRepository programRepository;

    @Transactional(readOnly = true)
    public List<AdminUserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(AdminUserDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public User updateUserStatus(UUID userId, UpdateUserStatusRequest request) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (currentUser.getId().equals(userId)) {
            throw new IllegalArgumentException("Admin cannot change their own status.");
        }

        User userToUpdate = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        userToUpdate.setStatus(request.getStatus());
        return userRepository.save(userToUpdate);
    }

    @Transactional(readOnly = true)
    public PlatformAnalyticsDto getPlatformAnalytics() {
        long totalUsers = userRepository.count();
        long totalHackers = userRepository.countByRole("hacker");
        // Assuming the simplified model where an org is a user with the 'organization' role
        long totalOrgs = userRepository.countByRole("organization");
        long totalReports = reportRepository.count();
        long totalPrograms = programRepository.count();

        PlatformAnalyticsDto.Kpis kpis = PlatformAnalyticsDto.Kpis.builder()
                .totalUsers(totalUsers)
                .totalHackers(totalHackers)
                .totalOrgs(totalOrgs)
                .totalReports(totalReports)
                .totalPrograms(totalPrograms)
                .build();

        // This is a simplified version. A real implementation might use a more efficient query.
        Map<String, Long> reportsByStatus = reportRepository.findAll().stream()
                .collect(Collectors.groupingBy(Report::getStatus, Collectors.counting()));

        return PlatformAnalyticsDto.builder()
                .kpis(kpis)
                .reportsByStatus(reportsByStatus)
                .build();
    }
}
