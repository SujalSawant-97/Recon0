package com.example.Recon0.services;

import com.example.Recon0.dto.ProgramDto;
import com.example.Recon0.dto.organization.CreateProgramRequest;
import com.example.Recon0.dto.organization.OrgDashboardDto;
import com.example.Recon0.dto.organization.ProgramAnalyticsDto;
import com.example.Recon0.dto.organization.UpdateReportStatusRequest;
import com.example.Recon0.dto.reports.ReportDto;
import com.example.Recon0.models.Organization;
import com.example.Recon0.models.Program;
import com.example.Recon0.models.Report;
import com.example.Recon0.models.User;
//import com.example.Recon0.repositories.OrganizationRepository;
import com.example.Recon0.repositories.ProgramRepository;
import com.example.Recon0.repositories.ReportRepository;
import com.example.Recon0.repositories.UserRepository;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrganizationService {

    //private final OrganizationRepository organizationRepository;
    private final ProgramRepository programRepository;
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final  NotificationService notificationService;
    private final AchievementService achievementService;

    public OrganizationService(  ProgramRepository programRepository, ReportRepository reportRepository, UserRepository userRepository,NotificationService notificationService,AchievementService achievementService) {
        //this.organizationRepository = organizationRepository;
        this.programRepository = programRepository;
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.notificationService=notificationService;
        this.achievementService=achievementService;
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
//    private Organization getCurrentUsersOrganization() {
//        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        // Assuming a user is linked to one primary organization
//        return organizationRepository.findByOwnerId(currentUser.getId())
//                .orElseThrow(() -> new IllegalStateException("The current user is not associated with any organization."));
//    }

    @Transactional(readOnly = true)
    public OrgDashboardDto getOrganizationDashboard() {
        User currentUser = getCurrentUser();

        // 1. Find all programs owned by this user.
        List<Program> programs = programRepository.findByOrganization(currentUser);
        long programCount = programs.size();

        // If there are no programs, the other KPIs are zero.
        long totalReports = 0;
        long newReports = 0;
        List<OrgDashboardDto.RecentReportDto> recentReports = Collections.emptyList();

        if (!programs.isEmpty()) {
            // 2. Calculate KPIs using the list of programs.
            totalReports = reportRepository.countByProgramIn(programs);
            newReports = reportRepository.countByProgramInAndStatus(programs, "New");

            // 3. Fetch the 5 most recent reports.
            List<Report> recentReportEntities = reportRepository.findTop5ByProgramInOrderByCreatedAtDesc(programs);
            recentReports = recentReportEntities.stream()
                    .map(report -> OrgDashboardDto.RecentReportDto.builder()
                            .id(report.getId().toString())
                            .title(report.getTitle())
                            .severity(report.getSeverity())
                            .status(report.getStatus())
                            .createdAt(report.getCreatedAt().toString())
                            .build())
                    .collect(Collectors.toList());
        }

        // 4. Assemble the KPI DTO.
        OrgDashboardDto.KpiDto kpis = OrgDashboardDto.KpiDto.builder()
                .programCount(programCount)
                .totalReports(totalReports)
                .newReports(newReports)
                .build();

        // 5. Build and return the final Dashboard DTO.
        return OrgDashboardDto.builder()
                .kpis(kpis)
                .recentReports(recentReports)
                .build();
    }
    @Transactional(readOnly = true)
    public ProgramAnalyticsDto getProgramAnalytics(UUID programId) {
        User currentUser = getCurrentUser();
        Program program = programRepository.findById(programId)
                .orElseThrow(() -> new RuntimeException("Program not found"));

        // Security Check: Ensure the user requesting analytics owns the program.
        if (!program.getOrganization_id().equals(currentUser)) {
            throw new AccessDeniedException("You do not have permission to view analytics for this program.");
        }

        // Fetch KPI data
        long totalReports = reportRepository.countByProgram(program);
        long resolvedReports = reportRepository.countByProgramAndStatus(program, "Resolved");

        ProgramAnalyticsDto.KpiDto kpis = ProgramAnalyticsDto.KpiDto.builder()
                .totalReports(totalReports)
                .resolvedReports(resolvedReports)
                // These are complex metrics, mocked as per the API contract for now.
                .avgTimeToBountyDays(14)
                .totalPaidOut(25400)
                .build();

        // Fetch reports grouped by severity
        List<Map<String, Object>> reportsBySeverity = reportRepository.countReportsBySeverity(program);

        // Build the final DTO
        return ProgramAnalyticsDto.builder()
                .programTitle(program.getTitle())
                .kpis(kpis)
                .reportsBySeverity(reportsBySeverity)
                .build();
    }

    @Transactional
    @CacheEvict(value = "programs", allEntries = true)
    public ProgramDto createProgram(CreateProgramRequest request) {
        User currentUser = getCurrentUser();
//        Organization org = organizationRepository.findByOwnerId(currentUser.getId())
//                .orElseThrow(() -> new RuntimeException("Organization not found for current user"));

        Program program = new Program();
        program.setOrganization_id(currentUser);
        program.setOrg_name(currentUser.getFull_name());
        program.setTitle(request.getTitle());
        program.setDescription(request.getDescription());
        program.setPolicy(request.getPolicy());
        program.setScope(request.getScope());
        program.setOut_of_scope(request.getOut_of_scope());
        program.setMin_bounty(request.getMin_bounty());
        program.setMax_bounty(request.getMax_bounty());
        program.setTags(request.getTags());

        Program savedProgram = programRepository.save(program);
        return ProgramDto.fromProgram(savedProgram);
    }

    @Transactional(readOnly = true)
    public List<ProgramDto> getMyPrograms() {
        User organization = getCurrentUser();
        List<Program> programs = programRepository.findByOrganization(organization);

        return programs.stream()
                .map(ProgramDto::fromProgram) // Use the DTO conversion helper
                .collect(Collectors.toList());
    }

    @Transactional
    public ReportDto updateReportStatus(UUID reportId, UpdateReportStatusRequest request) {
        User currentUser = getCurrentUser();
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found with ID: " + reportId));

        // Security check: Ensure the current user owns the program this report belongs to.
        if (!report.getProgram().getOrganization_id().equals(currentUser)) {
            throw new AccessDeniedException("You do not have permission to modify this report.");
        }

        String oldStatus = report.getStatus();
        String newStatus = request.getStatus();

        // Award reputation points only if the status is changing to "Accepted" for the first time.
        if ("Accepted".equalsIgnoreCase(newStatus) && !"Accepted".equalsIgnoreCase(oldStatus)) {
            User reporter = report.getReporter();
            int pointsToAdd = calculatePointsForSeverity(report.getSeverity());

            reporter.setReputationPoints(reporter.getReputationPoints() + pointsToAdd);
            userRepository.save(reporter);

            // Create a notification for the hacker
            String message = String.format("Your report '%s' was accepted! You earned %d reputation points.", report.getTitle(), pointsToAdd);
            notificationService.createNotification(reporter, "report_update", message);
            achievementService.checkAndAwardAchievements(report);
        }

        report.setStatus(newStatus);
        Report updatedReport = reportRepository.save(report);
        return ReportDto.fromReport(updatedReport);
    }


    private int calculatePointsForSeverity(String severity) {
        switch (severity.toLowerCase()) {
            case "critical": return 50;
            case "high": return 25;
            case "medium": return 10;
            case "low": return 5;
            default: return 0;
        }
    }
}
