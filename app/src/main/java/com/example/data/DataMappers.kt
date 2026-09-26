package com.example.data

import com.example.model.*
import org.json.JSONArray
import org.json.JSONObject

object DataMappers {

    // --- Slide Entity Mapping ---
    fun toSlideEntity(slide: Slide, presentationId: String): SlideEntity {
        val bulletsJson = JSONArray(slide.bullets).toString()
        val keyPointsJson = JSONArray(slide.speakingGuide.keyPoints).toString()

        val visualDataObj = JSONObject().apply {
            val stepsArr = JSONArray()
            slide.visualData.steps.forEach { step ->
                stepsArr.put(JSONObject().apply {
                    put("number", step.number)
                    put("title", step.title)
                    put("description", step.description)
                })
            }
            put("steps", stepsArr)

            put("comparisonLeftTitle", slide.visualData.comparisonLeftTitle)
            put("comparisonLeft", JSONArray(slide.visualData.comparisonLeft))
            put("comparisonRightTitle", slide.visualData.comparisonRightTitle)
            put("comparisonRight", JSONArray(slide.visualData.comparisonRight))

            val timelineArr = JSONArray()
            slide.visualData.timelineEvents.forEach { ev ->
                timelineArr.put(JSONObject().apply {
                    put("phase", ev.phase)
                    put("title", ev.title)
                    put("description", ev.description)
                })
            }
            put("timelineEvents", timelineArr)

            val metricsArr = JSONArray()
            slide.visualData.metrics.forEach { m ->
                metricsArr.put(JSONObject().apply {
                    put("value", m.value)
                    put("label", m.label)
                    put("note", m.note)
                })
            }
            put("metrics", metricsArr)

            put("cycleNodes", JSONArray(slide.visualData.cycleNodes))
            put("hierarchyNodes", JSONArray(slide.visualData.hierarchyNodes))
        }

        return SlideEntity(
            id = slide.id,
            presentationId = presentationId,
            index = slide.index,
            title = slide.title,
            subtitle = slide.subtitle,
            layoutType = slide.layoutType.name,
            bulletsJson = bulletsJson,
            visualType = slide.visualType.name,
            visualDataJson = visualDataObj.toString(),
            openingLine = slide.speakingGuide.openingLine,
            whatToSay = slide.speakingGuide.whatToSay,
            keyPointsJson = keyPointsJson,
            transition = slide.speakingGuide.transition,
            exampleNote = slide.speakingGuide.exampleNote,
            estimatedSeconds = slide.estimatedSeconds
        )
    }

    fun toSlideDomain(entity: SlideEntity): Slide {
        val bulletsList = mutableListOf<String>()
        try {
            val arr = JSONArray(entity.bulletsJson)
            for (i in 0 until arr.length()) {
                bulletsList.add(arr.getString(i))
            }
        } catch (_: Exception) {}

        val keyPointsList = mutableListOf<String>()
        try {
            val arr = JSONArray(entity.keyPointsJson)
            for (i in 0 until arr.length()) {
                keyPointsList.add(arr.getString(i))
            }
        } catch (_: Exception) {}

        val visualData = parseVisualData(entity.visualDataJson)

        val speakingGuide = SpeakingGuide(
            openingLine = entity.openingLine,
            whatToSay = entity.whatToSay,
            keyPoints = keyPointsList,
            transition = entity.transition,
            exampleNote = entity.exampleNote,
            targetSeconds = entity.estimatedSeconds
        )

        val layout = try {
            SlideLayoutType.valueOf(entity.layoutType)
        } catch (_: Exception) {
            SlideLayoutType.STANDARD
        }

        val visual = try {
            VisualType.valueOf(entity.visualType)
        } catch (_: Exception) {
            VisualType.NONE
        }

        return Slide(
            id = entity.id,
            presentationId = entity.presentationId,
            index = entity.index,
            title = entity.title,
            subtitle = entity.subtitle,
            layoutType = layout,
            bullets = bulletsList,
            visualType = visual,
            visualData = visualData,
            speakingGuide = speakingGuide,
            estimatedSeconds = entity.estimatedSeconds
        )
    }

    private fun parseVisualData(jsonStr: String): VisualData {
        if (jsonStr.isBlank()) return VisualData()
        return try {
            val obj = JSONObject(jsonStr)

            val steps = mutableListOf<ProcessStep>()
            val stepsArr = obj.optJSONArray("steps")
            if (stepsArr != null) {
                for (i in 0 until stepsArr.length()) {
                    val s = stepsArr.getJSONObject(i)
                    steps.add(
                        ProcessStep(
                            number = s.optInt("number", i + 1),
                            title = s.optString("title"),
                            description = s.optString("description")
                        )
                    )
                }
            }

            val compLeft = mutableListOf<String>()
            val clArr = obj.optJSONArray("comparisonLeft")
            if (clArr != null) {
                for (i in 0 until clArr.length()) compLeft.add(clArr.getString(i))
            }

            val compRight = mutableListOf<String>()
            val crArr = obj.optJSONArray("comparisonRight")
            if (crArr != null) {
                for (i in 0 until crArr.length()) compRight.add(crArr.getString(i))
            }

            val timeline = mutableListOf<TimelineEvent>()
            val tlArr = obj.optJSONArray("timelineEvents")
            if (tlArr != null) {
                for (i in 0 until tlArr.length()) {
                    val t = tlArr.getJSONObject(i)
                    timeline.add(
                        TimelineEvent(
                            phase = t.optString("phase"),
                            title = t.optString("title"),
                            description = t.optString("description")
                        )
                    )
                }
            }

            val metrics = mutableListOf<MetricItem>()
            val mArr = obj.optJSONArray("metrics")
            if (mArr != null) {
                for (i in 0 until mArr.length()) {
                    val m = mArr.getJSONObject(i)
                    metrics.add(
                        MetricItem(
                            value = m.optString("value"),
                            label = m.optString("label"),
                            note = m.optString("note")
                        )
                    )
                }
            }

            val cycle = mutableListOf<String>()
            val cArr = obj.optJSONArray("cycleNodes")
            if (cArr != null) {
                for (i in 0 until cArr.length()) cycle.add(cArr.getString(i))
            }

            val hierarchy = mutableListOf<String>()
            val hArr = obj.optJSONArray("hierarchyNodes")
            if (hArr != null) {
                for (i in 0 until hArr.length()) hierarchy.add(hArr.getString(i))
            }

            VisualData(
                steps = steps,
                comparisonLeftTitle = obj.optString("comparisonLeftTitle"),
                comparisonLeft = compLeft,
                comparisonRightTitle = obj.optString("comparisonRightTitle"),
                comparisonRight = compRight,
                timelineEvents = timeline,
                metrics = metrics,
                cycleNodes = cycle,
                hierarchyNodes = hierarchy
            )
        } catch (_: Exception) {
            VisualData()
        }
    }

    // --- Presentation Entity Mapping ---
    fun toPresentationEntity(presentation: Presentation): PresentationEntity {
        return PresentationEntity(
            id = presentation.id,
            title = presentation.title,
            topic = presentation.topic,
            summary = presentation.summary,
            style = presentation.style.name,
            theme = presentation.theme.name,
            targetDurationSeconds = presentation.targetDurationSeconds,
            slideCount = presentation.slides.size,
            createdAt = presentation.createdAt,
            updatedAt = presentation.updatedAt
        )
    }

    fun toPresentationDomain(entity: PresentationEntity, slides: List<Slide>): Presentation {
        val style = try {
            PresentationStyle.valueOf(entity.style)
        } catch (_: Exception) {
            PresentationStyle.PROFESSIONAL
        }

        val theme = try {
            SlideThemeType.valueOf(entity.theme)
        } catch (_: Exception) {
            SlideThemeType.MODERN_ACADEMIC
        }

        return Presentation(
            id = entity.id,
            title = entity.title,
            topic = entity.topic,
            summary = entity.summary,
            style = style,
            theme = theme,
            targetDurationSeconds = entity.targetDurationSeconds,
            slides = slides,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    // --- Practice Session Mapping ---
    fun toPracticeSessionEntity(session: PracticeSession): PracticeSessionEntity {
        val feedbackArr = JSONArray()
        session.slideFeedback.forEach { fb ->
            feedbackArr.put(JSONObject().apply {
                put("slideIndex", fb.slideIndex)
                put("slideTitle", fb.slideTitle)
                put("actualSeconds", fb.actualSeconds)
                put("targetSeconds", fb.targetSeconds)
                put("issue", fb.issue)
                put("tryInstead", fb.tryInstead)
                put("deliveredTranscript", fb.deliveredTranscript)
                put("readingDetected", fb.readingDetected)
                put("keyPointsHit", fb.keyPointsHit)
                put("totalKeyPoints", fb.totalKeyPoints)
            })
        }

        val weakArr = JSONArray()
        session.weakSections.forEach { ws ->
            weakArr.put(JSONObject().apply {
                put("slideIndex", ws.slideIndex)
                put("topic", ws.topic)
                put("originalDelivery", ws.originalDelivery)
                put("whatCouldImprove", ws.whatCouldImprove)
                put("betterApproach", ws.betterApproach)
                put("practiceVersion", ws.practiceVersion)
            })
        }

        val revArr = JSONArray()
        session.slideRevisions.forEach { rev ->
            revArr.put(JSONObject().apply {
                put("slideIndex", rev.slideIndex)
                put("observation", rev.observation)
                put("actionType", rev.actionType)
                put("suggestionText", rev.suggestionText)
                put("previewNewTitle", rev.previewNewTitle)
            })
        }

        val planArr = JSONArray()
        session.smartPracticePlan.forEach { pl ->
            planArr.put(JSONObject().apply {
                put("durationMinutes", pl.durationMinutes)
                put("focusArea", pl.focusArea)
                put("guidance", pl.guidance)
            })
        }

        return PracticeSessionEntity(
            id = session.id,
            presentationId = session.presentationId,
            timestamp = session.timestamp,
            durationSeconds = session.durationSeconds,
            targetDurationSeconds = session.targetDurationSeconds,
            overallScore = session.overallScore,
            paceWpm = session.paceWpm,
            fillerCount = session.fillerCount,
            clarityScore = session.clarityScore,
            pacingScore = session.pacingScore,
            fillerScore = session.fillerScore,
            coverageScore = session.coverageScore,
            engagementScore = session.engagementScore,
            slideReadingPercentage = session.slideReadingPercentage,
            transcript = session.transcript,
            slideFeedbackJson = feedbackArr.toString(),
            weakSectionsJson = weakArr.toString(),
            slideRevisionsJson = revArr.toString(),
            smartPracticePlanJson = planArr.toString()
        )
    }

    fun toPracticeSessionDomain(entity: PracticeSessionEntity): PracticeSession {
        val feedbackList = mutableListOf<SlidePracticeFeedback>()
        try {
            val arr = JSONArray(entity.slideFeedbackJson)
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                feedbackList.add(
                    SlidePracticeFeedback(
                        slideIndex = o.optInt("slideIndex"),
                        slideTitle = o.optString("slideTitle"),
                        actualSeconds = o.optInt("actualSeconds"),
                        targetSeconds = o.optInt("targetSeconds"),
                        issue = o.optString("issue"),
                        tryInstead = o.optString("tryInstead"),
                        deliveredTranscript = o.optString("deliveredTranscript"),
                        readingDetected = o.optBoolean("readingDetected"),
                        keyPointsHit = o.optInt("keyPointsHit", 2),
                        totalKeyPoints = o.optInt("totalKeyPoints", 3)
                    )
                )
            }
        } catch (_: Exception) {}

        val weakList = mutableListOf<WeakSectionFeedback>()
        try {
            val arr = JSONArray(entity.weakSectionsJson)
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                weakList.add(
                    WeakSectionFeedback(
                        slideIndex = o.optInt("slideIndex"),
                        topic = o.optString("topic"),
                        originalDelivery = o.optString("originalDelivery"),
                        whatCouldImprove = o.optString("whatCouldImprove"),
                        betterApproach = o.optString("betterApproach"),
                        practiceVersion = o.optString("practiceVersion")
                    )
                )
            }
        } catch (_: Exception) {}

        val revList = mutableListOf<SlideRevisionSuggestion>()
        try {
            val arr = JSONArray(entity.slideRevisionsJson)
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                revList.add(
                    SlideRevisionSuggestion(
                        slideIndex = o.optInt("slideIndex"),
                        observation = o.optString("observation"),
                        actionType = o.optString("actionType"),
                        suggestionText = o.optString("suggestionText"),
                        previewNewTitle = o.optString("previewNewTitle")
                    )
                )
            }
        } catch (_: Exception) {}

        val planList = mutableListOf<PracticePlanItem>()
        try {
            val arr = JSONArray(entity.smartPracticePlanJson)
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                planList.add(
                    PracticePlanItem(
                        durationMinutes = o.optInt("durationMinutes", 2),
                        focusArea = o.optString("focusArea"),
                        guidance = o.optString("guidance")
                    )
                )
            }
        } catch (_: Exception) {}

        return PracticeSession(
            id = entity.id,
            presentationId = entity.presentationId,
            timestamp = entity.timestamp,
            durationSeconds = entity.durationSeconds,
            targetDurationSeconds = entity.targetDurationSeconds,
            overallScore = entity.overallScore,
            paceWpm = entity.paceWpm,
            fillerCount = entity.fillerCount,
            clarityScore = entity.clarityScore,
            pacingScore = entity.pacingScore,
            fillerScore = entity.fillerScore,
            coverageScore = entity.coverageScore,
            engagementScore = entity.engagementScore,
            slideReadingPercentage = entity.slideReadingPercentage,
            transcript = entity.transcript,
            slideFeedback = feedbackList,
            weakSections = weakList,
            slideRevisions = revList,
            smartPracticePlan = planList
        )
    }
}
