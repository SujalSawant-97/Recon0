package com.example.Recon0.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "user_achievements")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAchievement {

    @EmbeddedId
    private UserAchievementId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("user") // This maps the 'user' part of the composite key
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("achievement") // This maps the 'achievement' part of the composite key
    @JoinColumn(name = "achievement_id")
    private Achievement achievement;

    @CreationTimestamp
    @Column(name = "earned_at", nullable = false, updatable = false)
    private OffsetDateTime earnedAt;

    // A convenience constructor that you can use in your service
    public UserAchievement(UserAchievementId id, User user, Achievement achievement) {
        this.id = id;
        this.user = user;
        this.achievement = achievement;
    }
}


