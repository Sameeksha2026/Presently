package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.GeminiPresentationService
import com.example.ai.OfflinePresentationEngine
import com.example.audio.SpeechCoachManager
import com.example.audio.SpeechCoachState
import com.example.data.AppDatabase
import com.example.data.PresentationRepository
import com.example.data.UserProgressEntity
import com.example.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

enum class Screen {
    HOME,
    UPLOAD,
    UNDERSTAND,
    EDITOR,
    PRESENTER,
    REPORT,
    PROGRESS
}

data class UiState(
    val currentScreen: Screen = Screen.HOME,
    val activePresentation: Presentation? = null,
    val activeSlideIndex: Int = 0,
    val contentAnalysis: ContentAnalysisResult? = null,
    val selectedOption: PresentationOption? = null,
    val selectedStyle: PresentationStyle = PresentationStyle.PROFESSIONAL,
    val selectedTheme: SlideThemeType = SlideThemeType.MODERN_ACADEMIC,
    val targetDurationMinutes: Int = 5,
    val secondsPerSlideChoice: Int = 45, // 15, 30, 45, 60
    val rawUploadedMaterial: String = "",
    val isAnalyzing: Boolean = false,
    val isGenerating: Boolean = false,
    val isImprovingSlide: Boolean = false,
    val activePracticeSession: PracticeSession? = null,
    val practiceElapsedSeconds: Int = 0,
    val slideElapsedSeconds: Int = 0,
    val isProUser: Boolean = false,
    val showProDialog: Boolean = false,
    val showPrivacyDialog: Boolean = false,
    val showExportDialog: Boolean = false,
    val showImproveSlideSheet: Boolean = false,
    val isCameraCoachingEnabled: Boolean = false,
    val statusMessage: String? = null
)

class PresentationViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PresentationRepository
    private val geminiService = GeminiPresentationService()
    val speechCoachManager: SpeechCoachManager = SpeechCoachManager(application)

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    val allPresentations: StateFlow<List<Presentation>>
    val userProgress: StateFlow<UserProgressEntity?>
    val speechState: StateFlow<SpeechCoachState> = speechCoachManager.state

    init {
        val db = AppDatabase.getDatabase(application)
        repository = PresentationRepository(db.appDao())
        allPresentations = repository.allPresentations.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
        userProgress = repository.userProgress.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), UserProgressEntity()
        )

        // Listen for practice timer ticks in Presenter mode
        viewModelScope.launch {
            while (true) {
                kotlinx.coroutines.delay(1000)
                if (_uiState.value.currentScreen == Screen.PRESENTER) {
                    _uiState.update { current ->
                        current.copy(
                            practiceElapsedSeconds = current.practiceElapsedSeconds + 1,
                            slideElapsedSeconds = current.slideElapsedSeconds + 1
                        )
                    }
                }
            }
        }
    }

    fun navigateTo(screen: Screen) {
        _uiState.update { it.copy(currentScreen = screen) }
    }

    fun setShowProDialog(show: Boolean) {
        _uiState.update { it.copy(showProDialog = show) }
    }

    fun setShowPrivacyDialog(show: Boolean) {
        _uiState.update { it.copy(showPrivacyDialog = show) }
    }

    fun setShowExportDialog(show: Boolean) {
        _uiState.update { it.copy(showExportDialog = show) }
    }

    fun setShowImproveSlideSheet(show: Boolean) {
        _uiState.update { it.copy(showImproveSlideSheet = show) }
    }

    fun toggleCameraCoaching() {
        _uiState.update { it.copy(isCameraCoachingEnabled = !it.isCameraCoachingEnabled) }
    }

    fun setStyle(style: PresentationStyle) {
        _uiState.update { it.copy(selectedStyle = style) }
    }

    fun setTheme(theme: SlideThemeType) {
        _uiState.update { current ->
            val updatedPres = current.activePresentation?.copy(theme = theme)
            current.copy(selectedTheme = theme, activePresentation = updatedPres)
        }
        _uiState.value.activePresentation?.let { pres ->
            viewModelScope.launch { repository.savePresentation(pres) }
        }
    }

    fun setSelectedOption(option: PresentationOption) {
        _uiState.update {
            it.copy(
                selectedOption = option,
                targetDurationMinutes = option.durationMinutes
            )
        }
    }

    fun setTargetDuration(minutes: Int) {
        _uiState.update { it.copy(targetDurationMinutes = minutes) }
    }

    fun setSecondsPerSlideChoice(seconds: Int) {
        _uiState.update { it.copy(secondsPerSlideChoice = seconds) }
    }

    fun setActiveSlideIndex(index: Int) {
        val pres = _uiState.value.activePresentation ?: return
        if (index in pres.slides.indices) {
            _uiState.update { it.copy(activeSlideIndex = index, slideElapsedSeconds = 0) }
            speechCoachManager.setActiveSlide(pres.slides[index])
        }
    }

    fun selectPresentation(presentation: Presentation) {
        viewModelScope.launch {
            repository.getPresentationWithSlides(presentation.id).firstOrNull()?.let { full ->
                _uiState.update {
                    it.copy(
                        activePresentation = full,
                        activeSlideIndex = 0,
                        selectedTheme = full.theme,
                        selectedStyle = full.style,
                        currentScreen = Screen.EDITOR
                    )
                }
            } ?: run {
                _uiState.update {
                    it.copy(
                        activePresentation = presentation,
                        activeSlideIndex = 0,
                        currentScreen = Screen.EDITOR
                    )
                }
            }
        }
    }

    // STEP 1: Upload Material -> Analyze Content
    fun analyzeMaterial(materialText: String) {
        if (materialText.isBlank()) return
        _uiState.update {
            it.copy(
                rawUploadedMaterial = materialText,
                isAnalyzing = true,
                currentScreen = Screen.UNDERSTAND
            )
        }
        viewModelScope.launch {
            val analysis = geminiService.analyzeContent(materialText)
            val defaultOption = analysis.suggestedOptions.getOrNull(1) ?: analysis.suggestedOptions.firstOrNull()
            _uiState.update {
                it.copy(
                    isAnalyzing = false,
                    contentAnalysis = analysis,
                    selectedOption = defaultOption,
                    targetDurationMinutes = defaultOption?.durationMinutes ?: 5
                )
            }
        }
    }

    // STEP 2: Generate Complete Structured Presentation & Speaking Guides
    fun generatePresentation() {
        val analysis = _uiState.value.contentAnalysis ?: return
        val option = _uiState.value.selectedOption ?: analysis.suggestedOptions.first()
        val style = _uiState.value.selectedStyle
        val theme = _uiState.value.selectedTheme
        val totalSeconds = _uiState.value.targetDurationMinutes * 60

        _uiState.update { it.copy(isGenerating = true) }

        viewModelScope.launch {
            val presentation = geminiService.generatePresentation(
                rawContent = _uiState.value.rawUploadedMaterial,
                analysis = analysis,
                chosenOption = option,
                style = style,
                theme = theme,
                totalDurationSeconds = totalSeconds
            )

            repository.savePresentation(presentation)

            // Update user progress
            val currentProg = userProgress.value ?: UserProgressEntity()
            repository.updateUserProgress(
                currentProg.copy(
                    totalPresentations = currentProg.totalPresentations + 1
                )
            )

            _uiState.update {
                it.copy(
                    isGenerating = false,
                    activePresentation = presentation,
                    activeSlideIndex = 0,
                    currentScreen = Screen.EDITOR
                )
            }
        }
    }

    // Try Demo Flow
    fun loadDemoPresentation() {
        val demoAnalysis = ContentAnalysisResult(
            topic = "Water Conservation & Resource Economics",
            mainConcepts = listOf(
                "Freshwater scarcity vs industrial demand",
                "Everyday wastage compounding effects",
                "Economic incentives for municipal recycling",
                "Distributed watershed restoration"
            ),
            importantPoints = listOf(
                "Less than 1% of Earth's water is readily accessible freshwater.",
                "Small individual actions scale to massive municipal impact.",
                "Circular water reuse models deliver 3x return on investment."
            ),
            suggestedLengthMinutes = 5,
            estimatedSlideCount = 7,
            suggestedOptions = listOf(
                PresentationOption(
                    id = "opt_story",
                    name = "Option 2 — The Story",
                    slideCount = 7,
                    bestFor = "Engaging audiences & leadership review",
                    durationMinutes = 5,
                    structureSummary = "Problem → Catalyst → Circular Economics → Global Impact",
                    audienceUnderstanding = "Audience understands urgency and acts on practical solutions."
                )
            )
        )

        val demoPres = OfflinePresentationEngine.generatePresentation(
            rawContent = "Water Conservation Matters",
            analysis = demoAnalysis,
            chosenOption = demoAnalysis.suggestedOptions.first(),
            style = PresentationStyle.STORYTELLING,
            theme = SlideThemeType.MODERN_ACADEMIC,
            totalDurationSeconds = 300
        )

        viewModelScope.launch {
            repository.savePresentation(demoPres)
            _uiState.update {
                it.copy(
                    activePresentation = demoPres,
                    activeSlideIndex = 0,
                    selectedTheme = SlideThemeType.MODERN_ACADEMIC,
                    selectedStyle = PresentationStyle.STORYTELLING,
                    currentScreen = Screen.EDITOR
                )
            }
        }
    }

    // Slide Editor Actions
    fun improveCurrentSlide(instruction: String) {
        val pres = _uiState.value.activePresentation ?: return
        val currentSlide = pres.slides.getOrNull(_uiState.value.activeSlideIndex) ?: return

        _uiState.update { it.copy(isImprovingSlide = true, showImproveSlideSheet = false) }

        viewModelScope.launch {
            val improved = geminiService.improveSlide(currentSlide, instruction)
            val updatedSlides = pres.slides.toMutableList()
            updatedSlides[_uiState.value.activeSlideIndex] = improved
            val updatedPres = pres.copy(slides = updatedSlides, updatedAt = System.currentTimeMillis())

            repository.savePresentation(updatedPres)
            _uiState.update {
                it.copy(
                    isImprovingSlide = false,
                    activePresentation = updatedPres
                )
            }
        }
    }

    fun updateSlideContent(title: String, subtitle: String, bullets: List<String>) {
        val pres = _uiState.value.activePresentation ?: return
        val currentSlide = pres.slides.getOrNull(_uiState.value.activeSlideIndex) ?: return
        val updatedSlide = currentSlide.copy(title = title, subtitle = subtitle, bullets = bullets)
        val updatedSlides = pres.slides.toMutableList()
        updatedSlides[_uiState.value.activeSlideIndex] = updatedSlide
        val updatedPres = pres.copy(slides = updatedSlides, updatedAt = System.currentTimeMillis())

        _uiState.update { it.copy(activePresentation = updatedPres) }
        viewModelScope.launch { repository.savePresentation(updatedPres) }
    }

    fun addSlide() {
        val pres = _uiState.value.activePresentation ?: return
        val newIndex = pres.slides.size + 1
        val newSlide = Slide(
            id = UUID.randomUUID().toString(),
            presentationId = pres.id,
            index = newIndex,
            title = "New Key Insight $newIndex",
            subtitle = "Supporting Context & Explanation",
            layoutType = SlideLayoutType.STANDARD,
            bullets = listOf("Primary takeaway point", "Supporting metric or example"),
            visualType = VisualType.NONE,
            speakingGuide = SpeakingGuide(
                openingLine = "Turning to our next critical consideration...",
                whatToSay = "Introduce this idea clearly. Relate it back to the overarching goal.",
                keyPoints = listOf("Introduce premise", "Provide evidence"),
                transition = "This transitions directly into our concluding thoughts."
            )
        )
        val updatedSlides = pres.slides + newSlide
        val updatedPres = pres.copy(slides = updatedSlides, updatedAt = System.currentTimeMillis())
        _uiState.update {
            it.copy(
                activePresentation = updatedPres,
                activeSlideIndex = updatedSlides.lastIndex
            )
        }
        viewModelScope.launch { repository.savePresentation(updatedPres) }
    }

    fun deleteCurrentSlide() {
        val pres = _uiState.value.activePresentation ?: return
        if (pres.slides.size <= 1) return // Keep at least one slide

        val updatedSlides = pres.slides.toMutableList()
        updatedSlides.removeAt(_uiState.value.activeSlideIndex)
        val reindexed = updatedSlides.mapIndexed { idx, slide -> slide.copy(index = idx + 1) }
        val updatedPres = pres.copy(slides = reindexed, updatedAt = System.currentTimeMillis())

        val newIndex = _uiState.value.activeSlideIndex.coerceAtMost(reindexed.lastIndex)
        _uiState.update {
            it.copy(
                activePresentation = updatedPres,
                activeSlideIndex = newIndex
            )
        }
        viewModelScope.launch { repository.savePresentation(updatedPres) }
    }

    fun moveSlide(up: Boolean) {
        val pres = _uiState.value.activePresentation ?: return
        val currentIndex = _uiState.value.activeSlideIndex
        val targetIndex = if (up) currentIndex - 1 else currentIndex + 1
        if (targetIndex !in pres.slides.indices) return

        val list = pres.slides.toMutableList()
        val temp = list[currentIndex]
        list[currentIndex] = list[targetIndex]
        list[targetIndex] = temp
        val reindexed = list.mapIndexed { idx, slide -> slide.copy(index = idx + 1) }
        val updatedPres = pres.copy(slides = reindexed, updatedAt = System.currentTimeMillis())

        _uiState.update {
            it.copy(
                activePresentation = updatedPres,
                activeSlideIndex = targetIndex
            )
        }
        viewModelScope.launch { repository.savePresentation(updatedPres) }
    }

    // PRESENTER & LIVE COACHING
    fun startLivePractice() {
        val pres = _uiState.value.activePresentation ?: return
        speechCoachManager.reset()
        _uiState.update {
            it.copy(
                currentScreen = Screen.PRESENTER,
                activeSlideIndex = 0,
                practiceElapsedSeconds = 0,
                slideElapsedSeconds = 0
            )
        }
        val firstSlide = pres.slides.firstOrNull() ?: return
        speechCoachManager.startListening(firstSlide)
    }

    fun nextPracticeSlide() {
        val pres = _uiState.value.activePresentation ?: return
        val nextIdx = _uiState.value.activeSlideIndex + 1
        if (nextIdx < pres.slides.size) {
            _uiState.update {
                it.copy(
                    activeSlideIndex = nextIdx,
                    slideElapsedSeconds = 0
                )
            }
            speechCoachManager.setActiveSlide(pres.slides[nextIdx])
        } else {
            finishLivePractice()
        }
    }

    fun prevPracticeSlide() {
        val pres = _uiState.value.activePresentation ?: return
        val prevIdx = _uiState.value.activeSlideIndex - 1
        if (prevIdx >= 0) {
            _uiState.update {
                it.copy(
                    activeSlideIndex = prevIdx,
                    slideElapsedSeconds = 0
                )
            }
            speechCoachManager.setActiveSlide(pres.slides[prevIdx])
        }
    }

    fun finishLivePractice() {
        speechCoachManager.stopListening()
        val pres = _uiState.value.activePresentation ?: return
        val transcript = speechCoachManager.state.value.liveTranscript
        val elapsed = _uiState.value.practiceElapsedSeconds.coerceAtLeast(30)

        viewModelScope.launch {
            val session = geminiService.analyzeDeliveredSpeech(
                presentation = pres,
                transcript = transcript,
                durationSeconds = elapsed
            )

            repository.savePracticeSession(session)

            // Update user progress metrics
            val prog = userProgress.value ?: UserProgressEntity()
            val newAvg = if (prog.averageScore > 0) (prog.averageScore + session.overallScore) / 2 else session.overallScore
            repository.updateUserProgress(
                prog.copy(
                    totalPracticeMinutes = prog.totalPracticeMinutes + (elapsed / 60).coerceAtLeast(1),
                    averageScore = newAvg,
                    lastPracticeDate = System.currentTimeMillis()
                )
            )

            _uiState.update {
                it.copy(
                    activePracticeSession = session,
                    currentScreen = Screen.REPORT
                )
            }
        }
    }

    // Apply Revision to Slide
    fun applySlideRevision(rev: SlideRevisionSuggestion) {
        val pres = _uiState.value.activePresentation ?: return
        val targetIdx = (rev.slideIndex - 1).coerceIn(pres.slides.indices)
        val slide = pres.slides[targetIdx]

        val updatedSlides = pres.slides.toMutableList()
        when (rev.actionType) {
            "SPLIT_SLIDE" -> {
                val newSlide = Slide(
                    id = UUID.randomUUID().toString(),
                    presentationId = pres.id,
                    index = rev.slideIndex + 1,
                    title = rev.previewNewTitle.ifBlank { "${slide.title} — Part 2" },
                    subtitle = "Detailed Deep Dive & Metrics",
                    layoutType = SlideLayoutType.STANDARD,
                    bullets = slide.bullets.drop(1).ifEmpty { listOf("Detailed supporting evidence") },
                    visualType = VisualType.METRIC_CHART,
                    speakingGuide = SpeakingGuide(
                        openingLine = "Carrying this thought further into the data...",
                        whatToSay = "Explain the specific metrics without feeling rushed.",
                        keyPoints = listOf("Metric context", "Impact projection")
                    )
                )
                updatedSlides[targetIdx] = slide.copy(bullets = slide.bullets.take(1))
                updatedSlides.add(targetIdx + 1, newSlide)
            }
            "SIMPLIFY_TEXT" -> {
                updatedSlides[targetIdx] = slide.copy(
                    bullets = slide.bullets.take(2).map { it.take(40) }
                )
            }
            else -> {
                updatedSlides[targetIdx] = OfflinePresentationEngine.improveSlide(slide, "visual diagram")
            }
        }

        val reindexed = updatedSlides.mapIndexed { idx, s -> s.copy(index = idx + 1) }
        val updatedPres = pres.copy(slides = reindexed, updatedAt = System.currentTimeMillis())

        _uiState.update { it.copy(activePresentation = updatedPres) }
        viewModelScope.launch { repository.savePresentation(updatedPres) }
    }

    fun deletePresentation(id: String) {
        viewModelScope.launch {
            repository.deletePresentation(id)
            if (_uiState.value.activePresentation?.id == id) {
                _uiState.update { it.copy(activePresentation = null, currentScreen = Screen.HOME) }
            }
        }
    }

    // Privacy & Data Deletion
    fun deleteAllPresentations() {
        viewModelScope.launch {
            repository.deleteAllPresentations()
            _uiState.update { it.copy(activePresentation = null, currentScreen = Screen.HOME) }
        }
    }

    fun deleteAllRecordingsAndTranscripts() {
        viewModelScope.launch {
            repository.deleteAllSessions()
            speechCoachManager.reset()
            _uiState.update { it.copy(activePracticeSession = null) }
        }
    }
}
