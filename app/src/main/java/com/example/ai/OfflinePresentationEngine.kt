package com.example.ai

import com.example.model.*
import java.util.UUID

object OfflinePresentationEngine {

    fun analyzeContent(rawContent: String): ContentAnalysisResult {
        val lines = rawContent.lines().filter { it.isNotBlank() }
        val topic = lines.firstOrNull()?.take(60)?.replace(Regex("[#*]"), "")?.trim()
            ?: "Modern Presentation Ideas"

        val sampleConcepts = listOf(
            "Core Architectural Principles & Foundations",
            "Real-World Impact & Application Patterns",
            "System Bottlenecks & Optimization Strategies",
            "Future Roadmap & Scalability Outlook"
        )

        val samplePoints = listOf(
            "Everyday small inefficiencies compound into major system losses.",
            "Visual diagrams and modular structures convey ideas 3x faster than raw text.",
            "Actionable metrics and clear transitions keep audiences engaged.",
            "Continuous practice and pacing control build natural speaker presence."
        )

        val options = listOf(
            PresentationOption(
                id = "opt_complete",
                name = "Option 1 — The Complete Explanation",
                slideCount = 10,
                bestFor = "Classroom or detailed seminar presentation",
                durationMinutes = 8,
                structureSummary = "Deep dive: Background → 4 Core Concepts → Case Studies → Q&A",
                audienceUnderstanding = "Audience gains thorough technical mastery and context."
            ),
            PresentationOption(
                id = "opt_story",
                name = "Option 2 — The Story",
                slideCount = 7,
                bestFor = "Engaging audiences & leadership meetings",
                durationMinutes = 5,
                structureSummary = "Narrative arc: The Current Crisis → The Breakthrough → Future Impact",
                audienceUnderstanding = "Audience is emotionally invested and remembers the key takeaway."
            ),
            PresentationOption(
                id = "opt_quick",
                name = "Option 3 — The Quick Version",
                slideCount = 5,
                bestFor = "3-minute lightning talk or executive pitch",
                durationMinutes = 3,
                structureSummary = "High-impact: The Problem → The Solution → Proof Points → Next Steps",
                audienceUnderstanding = "Audience immediately grasps the thesis and primary call-to-action."
            )
        )

        return ContentAnalysisResult(
            topic = topic,
            mainConcepts = sampleConcepts,
            importantPoints = samplePoints,
            suggestedLengthMinutes = 5,
            estimatedSlideCount = 7,
            suggestedOptions = options
        )
    }

    fun generatePresentation(
        rawContent: String,
        analysis: ContentAnalysisResult,
        chosenOption: PresentationOption,
        style: PresentationStyle,
        theme: SlideThemeType,
        totalDurationSeconds: Int
    ): Presentation {
        val presentationId = UUID.randomUUID().toString()
        val slideCount = chosenOption.slideCount
        val avgSecondsPerSlide = totalDurationSeconds / slideCount.coerceAtLeast(1)

        val slides = mutableListOf<Slide>()

        // 1. Title Slide
        slides.add(
            Slide(
                id = UUID.randomUUID().toString(),
                presentationId = presentationId,
                index = 1,
                title = analysis.topic,
                subtitle = "From Concept to Impact: Strategic Insights & Implementation",
                layoutType = SlideLayoutType.TITLE,
                bullets = listOf(
                    "Structured overview of critical concepts",
                    "Real-world data and practical demonstrations",
                    "Key takeaways and strategic action plan"
                ),
                visualType = VisualType.NONE,
                speakingGuide = SpeakingGuide(
                    openingLine = "Welcome everyone. Today we are going to explore ${analysis.topic}—not just as theory, but as an actionable capability.",
                    whatToSay = "Set the stage with confidence. Acknowledge why the audience is in the room and clarify that by the end of this talk, they will have a crystal-clear roadmap.",
                    keyPoints = listOf("Establish rapport", "State primary mission", "Preview the structure"),
                    transition = "Let's begin by examining the core challenge we face today.",
                    exampleNote = "Mention an everyday parallel so the room instantly relates.",
                    targetSeconds = avgSecondsPerSlide
                ),
                estimatedSeconds = avgSecondsPerSlide
            )
        )

        // 2. The Core Problem / Context
        slides.add(
            Slide(
                id = UUID.randomUUID().toString(),
                presentationId = presentationId,
                index = 2,
                title = "The Underlying Problem & Context",
                subtitle = "Why Current Approaches Break Down Under Pressure",
                layoutType = SlideLayoutType.STANDARD,
                bullets = listOf(
                    "Fragmented workflows increase cognitive overhead",
                    "Critical signals get lost in dense textual documentation",
                    "Actionable insights require structured visual synthesis"
                ),
                visualType = VisualType.METRIC_CHART,
                visualData = VisualData(
                    metrics = listOf(
                        MetricItem("68%", "Information loss without visuals", "Industry Benchmark"),
                        MetricItem("4.2x", "Faster retention via structured slides", "Cognitive Science Study"),
                        MetricItem("45s", "Audience attention span per idea", "Live Engagement Data")
                    )
                ),
                speakingGuide = SpeakingGuide(
                    openingLine = "Before we jump into solutions, let's understand why this problem matters so urgently.",
                    whatToSay = "Walk the audience through the friction point. People don't fail to understand because they lack intelligence; they struggle because traditional documentation is overwhelmingly dense.",
                    keyPoints = listOf("Cognitive overload", "Data retention cliff", "Need for structured synthesis"),
                    transition = "This brings us to our architectural approach and how we solve this.",
                    exampleNote = "Think about trying to read a 40-page report versus a crisp 5-step diagram.",
                    targetSeconds = avgSecondsPerSlide
                ),
                estimatedSeconds = avgSecondsPerSlide
            )
        )

        // 3. Process / Method Flowchart
        slides.add(
            Slide(
                id = UUID.randomUUID().toString(),
                presentationId = presentationId,
                index = 3,
                title = "System Architecture & Execution Flow",
                subtitle = "End-to-End Pipeline from Input to Verified Delivery",
                layoutType = SlideLayoutType.PROCESS,
                bullets = listOf(
                    "Ingest raw source data and isolate key arguments",
                    "Synthesize concepts into visual slide hierarchies",
                    "Simulate audience delivery with real-time feedback"
                ),
                visualType = VisualType.FLOWCHART,
                visualData = VisualData(
                    steps = listOf(
                        ProcessStep(1, "Ingest & Extract", "Extract concepts, statistics & relational links"),
                        ProcessStep(2, "Structure & Style", "Apply visual hierarchy and eliminate text clutter"),
                        ProcessStep(3, "Coached Delivery", "Real-time acoustic analysis and pacing tuning")
                    )
                ),
                speakingGuide = SpeakingGuide(
                    openingLine = "Here is the exact three-stage framework that turns chaos into clarity.",
                    whatToSay = "Point to each stage on the screen. Explain that step one parses the data, step two builds the narrative structure, and step three prepares the human speaker to shine.",
                    keyPoints = listOf("Pipeline flow", "Speaker-centric focus", "Iterative refinement"),
                    transition = "Let's compare this directly with traditional methodologies.",
                    exampleNote = "Similar to how a pilot rehearses in a flight simulator before takeoff.",
                    targetSeconds = avgSecondsPerSlide
                ),
                estimatedSeconds = avgSecondsPerSlide
            )
        )

        // 4. Comparison Graphic
        slides.add(
            Slide(
                id = UUID.randomUUID().toString(),
                presentationId = presentationId,
                index = 4,
                title = "Comparative Analysis: Traditional vs Modern",
                subtitle = "Evaluating Efficiency, Speaker Confidence & Audience Recall",
                layoutType = SlideLayoutType.COMPARISON,
                bullets = listOf(
                    "Traditional presentations treat slides as a script to read",
                    "Modern presentations treat slides as visual anchors that support the speaker"
                ),
                visualType = VisualType.COMPARISON,
                visualData = VisualData(
                    comparisonLeftTitle = "Traditional Method",
                    comparisonLeft = listOf(
                        "Wall of text on each slide",
                        "Speaker reads words verbatim",
                        "High filler words & awkward pacing",
                        "Audience tunes out in 3 minutes"
                    ),
                    comparisonRightTitle = "Presently AI Method",
                    comparisonRight = listOf(
                        "Visual diagrams & key metrics",
                        "Natural speaking prompts & cues",
                        "Live coaching on pace & fillers",
                        "Sustained audience retention"
                    )
                ),
                speakingGuide = SpeakingGuide(
                    openingLine = "When you place the traditional approach next to our method, the contrast is stark.",
                    whatToSay = "Highlight the left column first—we've all sat through presentations where the speaker reads every bullet point. Contrast that with the right column, where slides support rather than replace the speaker.",
                    keyPoints = listOf("Slide as anchor, not teleprompter", "Speaker freedom", "Engagement delta"),
                    transition = "Now let's examine the timeline of implementation.",
                    exampleNote = "Audiences read slides faster than you speak—if you put everything on the slide, you become redundant.",
                    targetSeconds = avgSecondsPerSlide
                ),
                estimatedSeconds = avgSecondsPerSlide
            )
        )

        // 5. Timeline / Milestones
        if (slideCount >= 5) {
            slides.add(
                Slide(
                    id = UUID.randomUUID().toString(),
                    presentationId = presentationId,
                    index = 5,
                    title = "Implementation Roadmap & Milestones",
                    subtitle = "Phased Rollout from Pilot to Enterprise Scale",
                    layoutType = SlideLayoutType.TIMELINE,
                    bullets = listOf(
                        "Phase 1: Core content ingestion & template calibration",
                        "Phase 2: Live speaker coaching pilots & baseline scoring",
                        "Phase 3: Organization-wide delivery with continuous metrics"
                    ),
                    visualType = VisualType.TIMELINE,
                    visualData = VisualData(
                        timelineEvents = listOf(
                            TimelineEvent("Phase 1", "Foundations", "Setup core models, ingest documents & train templates"),
                            TimelineEvent("Phase 2", "Speech Pilots", "Deploy live practice coach & measure filler word drop"),
                            TimelineEvent("Phase 3", "Full Scale", "Seamless presentation generation across all teams")
                        )
                    ),
                    speakingGuide = SpeakingGuide(
                        openingLine = "Rolling this out is a disciplined three-phase progression.",
                        whatToSay = "Explain that teams do not need to overhaul everything overnight. Phase 1 proves the slide structure, Phase 2 trains speaker confidence, and Phase 3 scales across the entire group.",
                        keyPoints = listOf("Phased rollout", "Low friction adoption", "Measurable wins at each step"),
                        transition = "As we look at the results, the cycle of continuous improvement becomes obvious.",
                        exampleNote = "Just 10 minutes of rehearsal on Phase 2 yields a 30% jump in delivery score.",
                        targetSeconds = avgSecondsPerSlide
                    ),
                    estimatedSeconds = avgSecondsPerSlide
                )
            )
        }

        // 6. Feedback Cycle
        if (slideCount >= 7) {
            slides.add(
                Slide(
                    id = UUID.randomUUID().toString(),
                    presentationId = presentationId,
                    index = 6,
                    title = "The Continuous Improvement Loop",
                    subtitle = "How Practice Directly Refines Slide Architecture",
                    layoutType = SlideLayoutType.CYCLE,
                    bullets = listOf(
                        "Presenter rehearsal identifies confusing sections",
                        "AI identifies slide bloat and over-explanation",
                        "Slides automatically simplify and re-balance"
                    ),
                    visualType = VisualType.CYCLE,
                    visualData = VisualData(
                        cycleNodes = listOf(
                            "Deliver Speech",
                            "AI Acoustic Analysis",
                            "Flag Bottlenecks",
                            "Refine Slides & Pacing"
                        )
                    ),
                    speakingGuide = SpeakingGuide(
                        openingLine = "What makes this unique is the feedback loop between speaker and slides.",
                        whatToSay = "Explain how when a speaker spends 90 seconds struggling on one slide, the AI realizes the slide is overloaded and proposes splitting it. The presentation adapts to human delivery.",
                        keyPoints = listOf("Two-way feedback", "Adaptive slide refinement", "Confidence compounding"),
                        transition = "Let's summarize the key takeaways before opening for questions.",
                        exampleNote = "Your rehearsal is no longer just practice—it's slide editing intelligence.",
                        targetSeconds = avgSecondsPerSlide
                    ),
                    estimatedSeconds = avgSecondsPerSlide
                )
            )
        }

        // Final. Summary & Q&A
        slides.add(
            Slide(
                id = UUID.randomUUID().toString(),
                presentationId = presentationId,
                index = slides.size + 1,
                title = "Summary & Strategic Takeaways",
                subtitle = "Empowering Presenters to Deliver with Clarity & Authority",
                layoutType = SlideLayoutType.CONCLUSION,
                bullets = listOf(
                    "Documents transform into visual, high-impact stories",
                    "Speaking cues empower speakers without forced memorization",
                    "AI coaching turns presentation anxiety into confident delivery"
                ),
                visualType = VisualType.NONE,
                speakingGuide = SpeakingGuide(
                    openingLine = "To close: great presentations are not born in slide templates; they come alive in the speaker's delivery.",
                    whatToSay = "Summarize the three core pillars with passion. Reiterate that the audience now has the exact tools to turn complex ideas into compelling presentations.",
                    keyPoints = listOf("Reinforce core promise", "Call to action", "Invite discussion"),
                    transition = "Thank you for your time. I'd love to take your questions now.",
                    exampleNote = "Pause, smile, and make direct eye contact across the room.",
                    targetSeconds = avgSecondsPerSlide
                ),
                estimatedSeconds = avgSecondsPerSlide
            )
        )

        return Presentation(
            id = presentationId,
            title = analysis.topic,
            topic = analysis.topic,
            summary = "Comprehensive presentation structured for ${chosenOption.bestFor} with live speaking guidance.",
            style = style,
            theme = theme,
            targetDurationSeconds = totalDurationSeconds,
            slides = slides,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
    }

    fun improveSlide(slide: Slide, instruction: String): Slide {
        return when {
            instruction.contains("simpler", ignoreCase = true) || instruction.contains("reduce", ignoreCase = true) -> {
                slide.copy(
                    bullets = slide.bullets.take(2).map { "• " + it.removePrefix("• ").take(45) },
                    speakingGuide = slide.speakingGuide.copy(
                        whatToSay = "Keep this punchy. State the core number and explain what it means in one sentence."
                    )
                )
            }
            instruction.contains("visual", ignoreCase = true) || instruction.contains("diagram", ignoreCase = true) -> {
                slide.copy(
                    visualType = if (slide.visualType == VisualType.NONE) VisualType.FLOWCHART else VisualType.COMPARISON,
                    visualData = VisualData(
                        steps = listOf(
                            ProcessStep(1, "Initiate", "Clear starting premise"),
                            ProcessStep(2, "Transform", "Systematic core action"),
                            ProcessStep(3, "Result", "Quantifiable positive impact")
                        )
                    )
                )
            }
            instruction.contains("example", ignoreCase = true) -> {
                slide.copy(
                    speakingGuide = slide.speakingGuide.copy(
                        exampleNote = "Real-world example: Consider how Apple keynote speakers use a single sentence per slide."
                    )
                )
            }
            instruction.contains("professional", ignoreCase = true) -> {
                slide.copy(
                    title = slide.title.replace("Problem", "Strategic Opportunity").replace("Chaos", "Complexity"),
                    subtitle = "Operational Efficiency & Executive Alignment"
                )
            }
            else -> slide
        }
    }

    fun analyzeDeliveredSpeech(
        presentation: Presentation,
        transcript: String,
        durationSeconds: Int
    ): PracticeSession {
        val wordCount = transcript.split(Regex("\\s+")).filter { it.isNotBlank() }.size
        val durationMinutes = durationSeconds / 60.0
        val paceWpm = if (durationMinutes > 0.1) (wordCount / durationMinutes).toInt() else 142

        // Detect filler words
        val fillerRegex = Regex("\\b(um|uh|like|you know|actually|basically|literally|so yeah|kind of)\\b", RegexOption.IGNORE_CASE)
        val fillers = fillerRegex.findAll(transcript).toList()
        val fillerCount = fillers.size.coerceAtLeast(1)

        // Pacing score
        val pacingScore = when {
            paceWpm in 130..165 -> 94
            paceWpm in 115..180 -> 82
            else -> 68
        }

        // Filler score
        val fillerScore = when {
            fillerCount <= 3 -> 95
            fillerCount <= 7 -> 84
            fillerCount <= 12 -> 72
            else -> 60
        }

        val clarityScore = 88
        val coverageScore = 86
        val engagementScore = 85
        val overallScore = ((pacingScore * 0.25) + (fillerScore * 0.25) + (clarityScore * 0.2) + (coverageScore * 0.15) + (engagementScore * 0.15)).toInt()

        val slideFeedbacks = presentation.slides.mapIndexed { idx, slide ->
            val isSlideReadDirectly = idx % 2 == 1 && transcript.contains(slide.title.take(10), ignoreCase = true)
            val issue = if (isSlideReadDirectly) {
                "You read most of the text directly from the slide."
            } else if (idx == 0) {
                "Slightly fast opening pace before setting context."
            } else {
                "Solid explanation with natural phrasing."
            }

            val advice = if (isSlideReadDirectly) {
                "Try instead: Explain the idea using the diagram and only mention the key number."
            } else if (idx == 0) {
                "Try instead: Take a two-second pause after stating your headline to let the room settle."
            } else {
                "Maintain this rhythm and continue connecting your examples to audience questions."
            }

            SlidePracticeFeedback(
                slideIndex = idx + 1,
                slideTitle = slide.title,
                actualSeconds = (durationSeconds / presentation.slides.size.coerceAtLeast(1)),
                targetSeconds = slide.estimatedSeconds,
                issue = issue,
                tryInstead = advice,
                deliveredTranscript = "Delivered explanation for ${slide.title}...",
                readingDetected = isSlideReadDirectly,
                keyPointsHit = if (isSlideReadDirectly) 1 else 3,
                totalKeyPoints = 3
            )
        }

        val weakSections = listOf(
            WeakSectionFeedback(
                slideIndex = 2,
                topic = "The Underlying Problem",
                originalDelivery = "So yeah, like, the problem is basically that people have too much text and stuff...",
                whatCouldImprove = "Remove 'like' and 'basically'; frame the issue as cognitive overload.",
                betterApproach = "Contrast visual processing speed against dense text.",
                practiceVersion = "When documents get too dense, audiences stop listening. Visual structures restore that connection."
            ),
            WeakSectionFeedback(
                slideIndex = 4,
                topic = "Comparative Analysis",
                originalDelivery = "Um, on the left we have traditional, and then on the right we have our method which is better.",
                whatCouldImprove = "Highlight specific benefits rather than just calling it 'better'.",
                betterApproach = "Use the anchor vs script contrast.",
                practiceVersion = "Notice how traditional slides force you to read, whereas structured anchors give you the freedom to speak."
            )
        )

        val revisions = listOf(
            SlideRevisionSuggestion(
                slideIndex = 2,
                observation = "You spent 75 seconds explaining multiple concepts on this slide.",
                actionType = "SPLIT_SLIDE",
                suggestionText = "Consider splitting this into two slides: one for the cognitive bottleneck, and one for the metric evidence.",
                previewNewTitle = "Cognitive Bottlenecks in Modern Presentations"
            ),
            SlideRevisionSuggestion(
                slideIndex = 3,
                observation = "You skipped the third bullet point in your verbal delivery.",
                actionType = "SIMPLIFY_TEXT",
                suggestionText = "Remove the third bullet to keep the slide 100% aligned with what you say.",
                previewNewTitle = "Simplified Pipeline"
            )
        )

        val practicePlan = listOf(
            PracticePlanItem(2, "Opening & Hook", "Rehearse Slide 1 opening line with a 2-second pause before continuing."),
            PracticePlanItem(2, "Slide 2 Problem Drill", "Focus on explaining the metrics without looking down at the notes."),
            PracticePlanItem(2, "Slide 4 Transitions", "Practice the bridge sentence from comparison to roadmap."),
            PracticePlanItem(2, "Filler Elimination", "Deliver Slide 3 silently counting to 1 instead of saying 'um'."),
            PracticePlanItem(2, "Full Timed Run", "One complete run-through strictly respecting the 5-minute timer.")
        )

        return PracticeSession(
            id = UUID.randomUUID().toString(),
            presentationId = presentation.id,
            timestamp = System.currentTimeMillis(),
            durationSeconds = durationSeconds,
            targetDurationSeconds = presentation.targetDurationSeconds,
            overallScore = overallScore,
            paceWpm = paceWpm,
            fillerCount = fillerCount,
            clarityScore = clarityScore,
            pacingScore = pacingScore,
            fillerScore = fillerScore,
            coverageScore = coverageScore,
            engagementScore = engagementScore,
            slideReadingPercentage = 18,
            transcript = transcript.ifBlank { "Sample practice transcript of delivered presentation with natural pacing and key points covered." },
            slideFeedback = slideFeedbacks,
            weakSections = weakSections,
            slideRevisions = revisions,
            smartPracticePlan = practicePlan
        )
    }
}
