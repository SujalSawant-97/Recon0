package com.example.Recon0.config;

import com.example.Recon0.models.Achievement;
import com.example.Recon0.repositories.AchievementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private AchievementRepository achievementRepository;

    @Override
    public void run(String... args) throws Exception {
        // Check if the achievements table is empty before seeding
        if (achievementRepository.count() == 0) {
            seedAchievements();
        }
    }

    private void seedAchievements() {
        List<Achievement> achievements = List.of(
                Achievement.builder().id("ach-1").name("First Find").description("Submit your first valid report.").icon("fa-flag").build(),
                Achievement.builder().id("ach-2").name("Critical Thinker").description("Submit a critical severity report.").icon("fa-skull-crossbones").build(),
                Achievement.builder().id("ach-3").name("Specialist").description("Submit 5 valid reports to a single program.").icon("fa-crosshairs").build(),
                Achievement.builder().id("ach-4").name("Bounty Hunter").description("Earn your first bounty.").icon("fa-sack-dollar").build(),
                Achievement.builder().id("ach-5").name("Consistent Contributor").description("Submit valid reports for 3 consecutive months.").icon("fa-calendar-alt").build()
        );
        achievementRepository.saveAll(achievements);
        System.out.println("Seeded " + achievements.size() + " achievements into the database.");
    }
}
