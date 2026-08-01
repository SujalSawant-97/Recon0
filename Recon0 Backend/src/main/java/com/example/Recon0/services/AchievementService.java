package com.example.Recon0.services;

import com.example.Recon0.models.*;
import com.example.Recon0.repositories.AchievementRepository;
import com.example.Recon0.repositories.ReportRepository;
import com.example.Recon0.repositories.UserAchievementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AchievementService {

    @Autowired
    private UserAchievementRepository userAchievementRepository;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private AchievementRepository achievementRepository;

    /**
     * This is the main method that checks all relevant achievements for a user
     * after one of their reports has been accepted.
     */
    @Transactional
    public void checkAndAwardAchievements(Report acceptedReport) {
        User reporter = acceptedReport.getReporter();

        // Get a list of achievements the user has already earned to avoid duplicate checks.
        List<String> earnedAchievementIds = getEarnedAchievementIds(reporter);

        // Check for "First Find" (ach-1)
        if (!earnedAchievementIds.contains("ach-1")) {
            // Pass the currently accepted report to the check method
            checkForFirstFind(reporter, acceptedReport);
        }

        // Check for "Critical Thinker" (ach-2)
        if (!earnedAchievementIds.contains("ach-2") && "Critical".equalsIgnoreCase(acceptedReport.getSeverity())) {
            awardAchievement(reporter, "ach-2", "Critical Thinker");
        }

        // Check for "Specialist" (ach-3)
        if (!earnedAchievementIds.contains("ach-3")) {
            checkForSpecialist(reporter, acceptedReport.getProgram());
        }
    }

    private void checkForFirstFind(User reporter, Report justAcceptedReport) {
        // Count how many OTHER reports are accepted, excluding the one we just processed.
        long otherAcceptedReports = reportRepository.countByReporterAndStatusIgnoreCaseAndIdNot(reporter, "Accepted", justAcceptedReport.getId());

        // If the count of OTHER accepted reports is 0, then this must be the first one.
        if (otherAcceptedReports == 0) {
            awardAchievement(reporter, "ach-1", "First Find");
        }
    }

    private void checkForSpecialist(User reporter, Program program) {
        // If the user has 5 or more accepted reports for this specific program
        if (reportRepository.countByReporterAndProgramAndStatusIgnoreCase(reporter, program, "Accepted") >= 5) {
            awardAchievement(reporter, "ach-3", "Specialist");
        }
    }

    private void awardAchievement(User user, String achievementId, String achievementName) {
        // Create the composite key
        UserAchievementId id = new UserAchievementId(user.getId(), achievementId);

        // Check if the user already has this achievement to be safe
        if (!userAchievementRepository.existsById(id)) {
            // FIXED: Fetch the full Achievement object from the database.
            Achievement achievement = achievementRepository.findById(achievementId)
                    .orElseThrow(() -> new RuntimeException("Achievement not found: " + achievementId));

            // Now, create the UserAchievement with the correct, non-null object.
            UserAchievement userAchievement = new UserAchievement(id, user, achievement);
            userAchievementRepository.save(userAchievement);

            // Notify the user
            String message = String.format("Congratulations! You've unlocked the '%s' achievement.", achievementName);
            notificationService.createNotification(user, "achievement_unlocked", message);
        }
    }

    private List<String> getEarnedAchievementIds(User user) {
        return userAchievementRepository.findByUser(user).stream()
                .map(ua -> ua.getId().getAchievement())
                .collect(Collectors.toList());
    }
}