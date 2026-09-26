package com.example.model

import java.util.UUID

enum class PresentationStyle(val displayName: String, val description: String) {
    ACADEMIC("Academic", "Clear, structured and suitable for school/college"),
    PROFESSIONAL("Professional", "Clean and formal for business & leadership"),
    STORYTELLING("Storytelling", "Starts with a problem and builds toward a conclusion"),
    PITCH("Pitch", "Problem → solution → impact → future"),
    VISUAL("Visual", "Less text, more diagrams and visual explanations"),
    EXAM_TEACHING("Exam / Teaching", "Concept → explanation → example → recap")
}

enum class SlideThemeType(val displayName: String) {
    MODERN_ACADEMIC("Modern Academic"),
    MINIMAL_PROFESSIONAL("Minimal Professional"),
    BOLD_STARTUP("Bold Startup"),
    DARK_TECH("Dark Tech"),
    CLEAN_EDUCATION("Clean Education"),
    ELEGANT_EDITORIAL("Elegant Editorial")
}

enum class SlideLayoutType {
    TITLE,
    STANDARD,
    PROCESS,
    COMPARISON,
    TIMELINE,
    CYCLE,
    METRICS,
    SUMMARY,
    CONCLUSION,
    QA
}

enum class VisualType {
    NONE,
    FLOWCHART,
    COMPARISON,
    TIMELINE,
    CYCLE,
    METRIC_CHART,
    HIERARCHY
}

data class ProcessStep(
    val number: Int,
    val title: String,
    val description: String
)

data class TimelineEvent(
    val phase: String,
    val title: String,
    val description: String
)

data class MetricItem(
    val value: String,
    val label: String,
    val note: String = ""
)

data class VisualData(
    val steps: List<ProcessStep> = emptyList(),
    val comparisonLeftTitle: String = "",
    val comparisonLeft: List<String> = emptyList(),
    val comparisonRightTitle: String = "",
    val comparisonRight: List<String> = emptyList(),
    val timelineEvents: List<TimelineEvent> = emptyList(),
    val metrics: List<MetricItem> = emptyList(),
    val cycleNodes: List<String> = emptyList(),
    val hierarchyNodes: List<String> = emptyList()
)

data class SpeakingGuide(
    val openingLine: String = "",
    val whatToSay: String = "",
    val keyPoints: List<String> = emptyList(),
    val transition: String = "",
    val exampleNote: String = "",
    val targetSeconds: Int = 45
)

data class Slide(
    val id: String = UUID.randomUUID().toString(),
    val presentationId: String = "",
    val index: Int = 0,
    val title: String = "",
    val subtitle: String = "",
    val layoutType: SlideLayoutType = SlideLayoutType.STANDARD,
    val bullets: List<String> = emptyList(),
    val visualType: VisualType = VisualType.NONE,
    val visualData: VisualData = VisualData(),
    val speakingGuide: SpeakingGuide = SpeakingGuide(),
    val estimatedSeconds: Int = 45
)

data class Presentation(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val topic: String = "",
    val summary: String = "",
    val style: PresentationStyle = PresentationStyle.PROFESSIONAL,
    val theme: SlideThemeType = SlideThemeType.MODERN_ACADEMIC,
    val targetDurationSeconds: Int = 300,
    val slides: List<Slide> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

data class PresentationOption(
    val id: String,
    val name: String,
    val slideCount: Int,
    val bestFor: String,
    val durationMinutes: Int,
    val structureSummary: String,
    val audienceUnderstanding: String
)

data class ContentAnalysisResult(
    val topic: String,
    val mainConcepts: List<String>,
    val importantPoints: List<String>,
    val suggestedLengthMinutes: Int,
    val estimatedSlideCount: Int,
    val suggestedOptions: List<PresentationOption>
)

data class SlidePracticeFeedback(
    val slideIndex: Int,
    val slideTitle: String,
    val actualSeconds: Int,
    val targetSeconds: Int,
    val issue: String,
    val tryInstead: String,
    val deliveredTranscript: String,
    val readingDetected: Boolean = false,
    val keyPointsHit: Int = 0,
    val totalKeyPoints: Int = 3
)

data class WeakSectionFeedback(
    val slideIndex: Int,
    val topic: String,
    val originalDelivery: String,
    val whatCouldImprove: String,
    val betterApproach: String,
    val practiceVersion: String
)

data class SlideRevisionSuggestion(
    val slideIndex: Int,
    val observation: String,
    val actionType: String, // "SPLIT_SLIDE", "SIMPLIFY_TEXT", "ADD_DIAGRAM"
    val suggestionText: String,
    val previewNewTitle: String = ""
)

data class PracticePlanItem(
    val durationMinutes: Int,
    val focusArea: String,
    val guidance: String
)

data class PracticeSession(
    val id: String = UUID.randomUUID().toString(),
    val presentationId: String,
    val timestamp: Long = System.currentTimeMillis(),
    val durationSeconds: Int = 0,
    val targetDurationSeconds: Int = 300,
    val overallScore: Int = 85,
    val paceWpm: Int = 145,
    val fillerCount: Int = 4,
    val clarityScore: Int = 88,
    val pacingScore: Int = 84,
    val fillerScore: Int = 82,
    val coverageScore: Int = 90,
    val engagementScore: Int = 86,
    val slideReadingPercentage: Int = 15,
    val transcript: String = "",
    val slideFeedback: List<SlidePracticeFeedback> = emptyList(),
    val weakSections: List<WeakSectionFeedback> = emptyList(),
    val slideRevisions: List<SlideRevisionSuggestion> = emptyList(),
    val smartPracticePlan: List<PracticePlanItem> = emptyList()
)
