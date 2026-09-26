package com.example.ai

import android.util.Log
import com.example.BuildConfig
import com.example.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiPresentationService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val apiKey: String
        get() = try {
            BuildConfig.GEMINI_API_KEY
        } catch (_: Throwable) {
            ""
        }

    private val isKeyConfigured: Boolean
        get() = apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY"

    suspend fun analyzeContent(rawContent: String): ContentAnalysisResult = withContext(Dispatchers.IO) {
        if (!isKeyConfigured) {
            return@withContext OfflinePresentationEngine.analyzeContent(rawContent)
        }

        val prompt = """
            You are Presently AI's presentation intelligence model.
            Analyze the following study material or text:
            "$rawContent"
            
            Return a JSON object with:
            {
               "topic": "Main topic name (max 6 words)",
               "mainConcepts": ["concept 1", "concept 2", "concept 3", "concept 4"],
               "importantPoints": ["point 1", "point 2", "point 3", "point 4"],
               "suggestedLengthMinutes": 5,
               "estimatedSlideCount": 7,
               "suggestedOptions": [
                  {
                    "id": "opt_complete",
                    "name": "Option 1 — The Complete Explanation",
                    "slideCount": 10,
                    "bestFor": "Classroom presentation or technical seminar",
                    "durationMinutes": 8,
                    "structureSummary": "Deep dive: Background → Core Concepts → Applications → Q&A",
                    "audienceUnderstanding": "Complete mastery of theory and practical implications."
                  },
                  {
                    "id": "opt_story",
                    "name": "Option 2 — The Story",
                    "slideCount": 7,
                    "bestFor": "Engaging audience and leadership review",
                    "durationMinutes": 5,
                    "structureSummary": "Narrative arc: Challenge → Paradigm Shift → Future Impact",
                    "audienceUnderstanding": "High emotional engagement and crisp memory of core thesis."
                  },
                  {
                    "id": "opt_quick",
                    "name": "Option 3 — The Quick Version",
                    "slideCount": 5,
                    "bestFor": "3-minute lightning talk or pitch",
                    "durationMinutes": 3,
                    "structureSummary": "Problem → Solution → Data → Next Step",
                    "audienceUnderstanding": "Instant clarity on the problem and decision criteria."
                  }
               ]
            }
            Output ONLY valid JSON.
        """.trimIndent()

        try {
            val responseText = callGeminiApi(prompt)
            if (responseText.isNotBlank()) {
                val cleanedJson = cleanJsonResponse(responseText)
                val json = JSONObject(cleanedJson)

                val topic = json.optString("topic", "Presentation Topic")
                val concepts = json.optJSONArray("mainConcepts")?.let { arr ->
                    List(arr.length()) { arr.getString(it) }
                } ?: emptyList()

                val points = json.optJSONArray("importantPoints")?.let { arr ->
                    List(arr.length()) { arr.getString(it) }
                } ?: emptyList()

                val lengthMin = json.optInt("suggestedLengthMinutes", 5)
                val slideCount = json.optInt("estimatedSlideCount", 7)

                val options = mutableListOf<PresentationOption>()
                val optArr = json.optJSONArray("suggestedOptions")
                if (optArr != null) {
                    for (i in 0 until optArr.length()) {
                        val optObj = optArr.getJSONObject(i)
                        options.add(
                            PresentationOption(
                                id = optObj.optString("id", "opt_$i"),
                                name = optObj.optString("name", "Option ${i + 1}"),
                                slideCount = optObj.optInt("slideCount", 7),
                                bestFor = optObj.optString("bestFor", "General audience"),
                                durationMinutes = optObj.optInt("durationMinutes", 5),
                                structureSummary = optObj.optString("structureSummary", ""),
                                audienceUnderstanding = optObj.optString("audienceUnderstanding", "")
                            )
                        )
                    }
                }

                if (options.isEmpty()) {
                    return@withContext OfflinePresentationEngine.analyzeContent(rawContent)
                }

                ContentAnalysisResult(
                    topic = topic,
                    mainConcepts = concepts.ifEmpty { listOf("Key Architecture", "Strategic Execution") },
                    importantPoints = points.ifEmpty { listOf("Modular workflows", "Clear visual anchors") },
                    suggestedLengthMinutes = lengthMin,
                    estimatedSlideCount = slideCount,
                    suggestedOptions = options
                )
            } else {
                OfflinePresentationEngine.analyzeContent(rawContent)
            }
        } catch (e: Exception) {
            Log.e("GeminiService", "Gemini analysis error, using offline engine: ${e.message}")
            OfflinePresentationEngine.analyzeContent(rawContent)
        }
    }

    suspend fun generatePresentation(
        rawContent: String,
        analysis: ContentAnalysisResult,
        chosenOption: PresentationOption,
        style: PresentationStyle,
        theme: SlideThemeType,
        totalDurationSeconds: Int
    ): Presentation = withContext(Dispatchers.IO) {
        // Return full rich presentation with speaking guidance and diagrams
        OfflinePresentationEngine.generatePresentation(
            rawContent, analysis, chosenOption, style, theme, totalDurationSeconds
        )
    }

    suspend fun improveSlide(slide: Slide, instruction: String): Slide = withContext(Dispatchers.IO) {
        if (!isKeyConfigured) {
            return@withContext OfflinePresentationEngine.improveSlide(slide, instruction)
        }

        val prompt = """
            Improve this presentation slide based on user instruction: "$instruction".
            Current Slide:
            Title: ${slide.title}
            Subtitle: ${slide.subtitle}
            Bullets: ${slide.bullets.joinToString("; ")}
            Speaking What to Say: ${slide.speakingGuide.whatToSay}
            
            Return JSON:
            {
               "title": "Improved Title",
               "subtitle": "Improved Subtitle",
               "bullets": ["Bullet 1", "Bullet 2"],
               "whatToSay": "Refined natural speaking guidance",
               "exampleNote": "Concrete real-world example"
            }
            Output ONLY valid JSON.
        """.trimIndent()

        try {
            val responseText = callGeminiApi(prompt)
            val cleaned = cleanJsonResponse(responseText)
            val json = JSONObject(cleaned)
            val title = json.optString("title", slide.title)
            val subtitle = json.optString("subtitle", slide.subtitle)
            val bullets = json.optJSONArray("bullets")?.let { arr ->
                List(arr.length()) { arr.getString(it) }
            } ?: slide.bullets
            val whatToSay = json.optString("whatToSay", slide.speakingGuide.whatToSay)
            val example = json.optString("exampleNote", slide.speakingGuide.exampleNote)

            slide.copy(
                title = title,
                subtitle = subtitle,
                bullets = bullets,
                speakingGuide = slide.speakingGuide.copy(
                    whatToSay = whatToSay,
                    exampleNote = example
                )
            )
        } catch (e: Exception) {
            OfflinePresentationEngine.improveSlide(slide, instruction)
        }
    }

    suspend fun analyzeDeliveredSpeech(
        presentation: Presentation,
        transcript: String,
        durationSeconds: Int
    ): PracticeSession = withContext(Dispatchers.IO) {
        OfflinePresentationEngine.analyzeDeliveredSpeech(
            presentation, transcript, durationSeconds
        )
    }

    private fun callGeminiApi(prompt: String): String {
        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
        val jsonPayload = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", prompt)
                        })
                    })
                })
            })
            put("generationConfig", JSONObject().apply {
                put("temperature", 0.4)
                put("responseMimeType", "application/json")
            })
        }

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = jsonPayload.toString().toRequestBody(mediaType)
        val request = Request.Builder().url(url).post(body).build()

        val response = client.newCall(request).execute()
        val respBody = response.body?.string() ?: ""
        if (!response.isSuccessful) {
            Log.e("GeminiService", "API call failed code ${response.code}: $respBody")
            return ""
        }

        val respJson = JSONObject(respBody)
        val candidates = respJson.optJSONArray("candidates") ?: return ""
        val candidate = candidates.optJSONObject(0) ?: return ""
        val content = candidate.optJSONObject("content") ?: return ""
        val parts = content.optJSONArray("parts") ?: return ""
        return parts.optJSONObject(0)?.optString("text") ?: ""
    }

    private fun cleanJsonResponse(raw: String): String {
        var str = raw.trim()
        if (str.startsWith("```json")) {
            str = str.removePrefix("```json")
        } else if (str.startsWith("```")) {
            str = str.removePrefix("```")
        }
        if (str.endsWith("```")) {
            str = str.removeSuffix("```")
        }
        return str.trim()
    }
}
