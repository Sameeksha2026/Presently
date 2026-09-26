package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.ExportDialog
import com.example.ui.components.PrivacyDialog
import com.example.ui.components.ProDialog
import com.example.ui.screens.*
import com.example.ui.theme.PresentlyAITheme
import com.example.viewmodel.PresentationViewModel
import com.example.viewmodel.Screen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PresentlyAITheme {
                PresentlyApp()
            }
        }
    }
}

@Composable
fun PresentlyApp(
    viewModel: PresentationViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val presentations by viewModel.allPresentations.collectAsStateWithLifecycle()
    val progress by viewModel.userProgress.collectAsStateWithLifecycle()
    val speechState by viewModel.speechState.collectAsStateWithLifecycle()

    // Handle Back Press Navigation
    BackHandler(enabled = uiState.currentScreen != Screen.HOME) {
        when (uiState.currentScreen) {
            Screen.UPLOAD -> viewModel.navigateTo(Screen.HOME)
            Screen.UNDERSTAND -> viewModel.navigateTo(Screen.UPLOAD)
            Screen.EDITOR -> viewModel.navigateTo(Screen.HOME)
            Screen.PRESENTER -> {
                viewModel.speechCoachManager.stopListening()
                viewModel.navigateTo(Screen.EDITOR)
            }
            Screen.REPORT -> viewModel.navigateTo(Screen.EDITOR)
            Screen.PROGRESS -> viewModel.navigateTo(Screen.HOME)
            Screen.HOME -> {}
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        when (uiState.currentScreen) {
            Screen.HOME -> {
                HomeScreen(
                    presentations = presentations,
                    progress = progress,
                    onCreatePresentation = { viewModel.navigateTo(Screen.UPLOAD) },
                    onPracticeClick = {
                        if (presentations.isNotEmpty()) {
                            viewModel.selectPresentation(presentations.first())
                            viewModel.startLivePractice()
                        } else {
                            viewModel.loadDemoPresentation()
                            viewModel.startLivePractice()
                        }
                    },
                    onTryDemoClick = {
                        viewModel.loadDemoPresentation()
                    },
                    onSelectPresentation = { pres ->
                        viewModel.selectPresentation(pres)
                    },
                    onOpenPro = { viewModel.setShowProDialog(true) },
                    onOpenPrivacy = { viewModel.setShowPrivacyDialog(true) },
                    onOpenProgress = { viewModel.navigateTo(Screen.PROGRESS) },
                    modifier = Modifier.fillMaxSize()
                )
            }
            Screen.UPLOAD -> {
                UploadScreen(
                    onBack = { viewModel.navigateTo(Screen.HOME) },
                    onAnalyzeContent = { materialText ->
                        viewModel.analyzeMaterial(materialText)
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
            Screen.UNDERSTAND -> {
                AnalysisScreen(
                    analysis = uiState.contentAnalysis,
                    selectedOption = uiState.selectedOption,
                    selectedStyle = uiState.selectedStyle,
                    selectedTheme = uiState.selectedTheme,
                    targetDurationMinutes = uiState.targetDurationMinutes,
                    secondsPerSlideChoice = uiState.secondsPerSlideChoice,
                    isGenerating = uiState.isGenerating,
                    onSelectOption = { viewModel.setSelectedOption(it) },
                    onSelectStyle = { viewModel.setStyle(it) },
                    onSelectTheme = { viewModel.setTheme(it) },
                    onSelectDuration = { viewModel.setTargetDuration(it) },
                    onSelectSecondsPerSlide = { viewModel.setSecondsPerSlideChoice(it) },
                    onGenerate = { viewModel.generatePresentation() },
                    onBack = { viewModel.navigateTo(Screen.UPLOAD) },
                    modifier = Modifier.fillMaxSize()
                )
            }
            Screen.EDITOR -> {
                EditorScreen(
                    presentation = uiState.activePresentation,
                    activeSlideIndex = uiState.activeSlideIndex,
                    isImprovingSlide = uiState.isImprovingSlide,
                    showImproveSheet = uiState.showImproveSlideSheet,
                    onSelectSlide = { viewModel.setActiveSlideIndex(it) },
                    onAddSlide = { viewModel.addSlide() },
                    onDeleteSlide = { viewModel.deleteCurrentSlide() },
                    onMoveSlide = { viewModel.moveSlide(it) },
                    onSelectTheme = { viewModel.setTheme(it) },
                    onOpenImproveSheet = { viewModel.setShowImproveSlideSheet(true) },
                    onCloseImproveSheet = { viewModel.setShowImproveSlideSheet(false) },
                    onImproveSlideAction = { action -> viewModel.improveCurrentSlide(action) },
                    onUpdateSlideContent = { title, sub, bullets ->
                        viewModel.updateSlideContent(title, sub, bullets)
                    },
                    onStartPractice = { viewModel.startLivePractice() },
                    onExport = { viewModel.setShowExportDialog(true) },
                    onBack = { viewModel.navigateTo(Screen.HOME) },
                    modifier = Modifier.fillMaxSize()
                )
            }
            Screen.PRESENTER -> {
                PresenterScreen(
                    presentation = uiState.activePresentation,
                    activeSlideIndex = uiState.activeSlideIndex,
                    practiceElapsedSeconds = uiState.practiceElapsedSeconds,
                    slideElapsedSeconds = uiState.slideElapsedSeconds,
                    speechState = speechState,
                    isCameraCoachingEnabled = uiState.isCameraCoachingEnabled,
                    onToggleCamera = { viewModel.toggleCameraCoaching() },
                    onNextSlide = { viewModel.nextPracticeSlide() },
                    onPrevSlide = { viewModel.prevPracticeSlide() },
                    onFinishPractice = { viewModel.finishLivePractice() },
                    onInjectSampleSpeech = { sampleText ->
                        viewModel.speechCoachManager.injectSimulatedTranscript(sampleText)
                    },
                    onBack = {
                        viewModel.speechCoachManager.stopListening()
                        viewModel.navigateTo(Screen.EDITOR)
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
            Screen.REPORT -> {
                ReportScreen(
                    session = uiState.activePracticeSession,
                    presentation = uiState.activePresentation,
                    onPractiseAgain = { viewModel.startLivePractice() },
                    onApplyRevision = { rev -> viewModel.applySlideRevision(rev) },
                    onExportReport = { viewModel.setShowExportDialog(true) },
                    onBack = { viewModel.navigateTo(Screen.EDITOR) },
                    modifier = Modifier.fillMaxSize()
                )
            }
            Screen.PROGRESS -> {
                ProgressScreen(
                    progress = progress,
                    sessions = emptyList(),
                    onBack = { viewModel.navigateTo(Screen.HOME) },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }

    // Modal Sheets
    if (uiState.showProDialog) {
        ProDialog(onDismiss = { viewModel.setShowProDialog(false) })
    }

    if (uiState.showPrivacyDialog) {
        PrivacyDialog(
            onDeletePresentations = { viewModel.deleteAllPresentations() },
            onDeleteRecordings = { viewModel.deleteAllRecordingsAndTranscripts() },
            onDismiss = { viewModel.setShowPrivacyDialog(false) }
        )
    }

    if (uiState.showExportDialog) {
        ExportDialog(
            presentation = uiState.activePresentation,
            onDismiss = { viewModel.setShowExportDialog(false) }
        )
    }
}
