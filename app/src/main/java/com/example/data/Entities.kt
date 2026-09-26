package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "presentations")
data class PresentationEntity(
    @PrimaryKey val id: String,
    val title: String,
    val topic: String,
    val summary: String,
    val style: String,
    val theme: String,
    val targetDurationSeconds: Int,
    val slideCount: Int,
    val createdAt: Long,
    val updatedAt: Long
)

@Entity(tableName = "slides")
data class SlideEntity(
    @PrimaryKey val id: String,
    val presentationId: String,
    val index: Int,
    val title: String,
    val subtitle: String,
    val layoutType: String,
    val bulletsJson: String,
    val visualType: String,
    val visualDataJson: String,
    val openingLine: String,
    val whatToSay: String,
    val keyPointsJson: String,
    val transition: String,
    val exampleNote: String,
    val estimatedSeconds: Int
)

@Entity(tableName = "practice_sessions")
data class PracticeSessionEntity(
    @PrimaryKey val id: String,
    val presentationId: String,
    val timestamp: Long,
    val durationSeconds: Int,
    val targetDurationSeconds: Int,
    val overallScore: Int,
    val paceWpm: Int,
    val fillerCount: Int,
    val clarityScore: Int,
    val pacingScore: Int,
    val fillerScore: Int,
    val coverageScore: Int,
    val engagementScore: Int,
    val slideReadingPercentage: Int,
    val transcript: String,
    val slideFeedbackJson: String,
    val weakSectionsJson: String,
    val slideRevisionsJson: String,
    val smartPracticePlanJson: String
)

@Entity(tableName = "user_progress")
data class UserProgressEntity(
    @PrimaryKey val id: Int = 1,
    val totalPresentations: Int = 0,
    val totalPracticeMinutes: Int = 0,
    val averageScore: Int = 0,
    val streakDays: Int = 1,
    val lastPracticeDate: Long = 0L,
    val isProUser: Boolean = false
)
