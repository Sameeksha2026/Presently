package com.example.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SpeechCoachState
import com.example.model.Presentation
import com.example.ui.components.AudioWaveform
import com.example.ui.components.SlideCard
import com.example.ui.components.SpeakingGuidePanel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PresenterScreen(
    presentation: Presentation?,
    activeSlideIndex: Int,
    practiceElapsedSeconds: Int,
    slideElapsedSeconds: Int,
    speechState: SpeechCoachState,
    isCameraCoachingEnabled: Boolean,
    onToggleCamera: () -> Unit,
    onNextSlide: () -> Unit,
    onPrevSlide: () -> Unit,
    onFinishPractice: () -> Unit,
    onInjectSampleSpeech: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (presentation == null) return

    val currentSlide = presentation.slides.getOrNull(activeSlideIndex) ?: return
    val nextSlide = presentation.slides.getOrNull(activeSlideIndex + 1)
    val totalTargetSeconds = presentation.targetDurationSeconds

    // Permission launcher for audio recording
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
    }

    val totalMins = practiceElapsedSeconds / 60
    val totalSecs = practiceElapsedSeconds % 60
    val targetMins = totalTargetSeconds / 60
    val targetSecs = totalTargetSeconds % 60
    val timeFormatted = String.format("%02d:%02d / %02d:%02d", totalMins, totalSecs, targetMins, targetSecs)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = PrimaryIndigo,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "LIVE PRACTICE",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Slide ${activeSlideIndex + 1} of ${presentation.slides.size}",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Exit Rehearsal")
                    }
                },
                actions = {
                    // Optional Visual Presence Coaching toggle
                    IconButton(onClick = onToggleCamera) {
                        Icon(
                            imageVector = if (isCameraCoachingEnabled) Icons.Default.Videocam else Icons.Default.VideocamOff,
                            contentDescription = "Visual Presence Coaching",
                            tint = if (isCameraCoachingEnabled) PrimaryIndigo else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onPrevSlide,
                        enabled = activeSlideIndex > 0,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBackIos, contentDescription = "Previous Slide", modifier = Modifier.size(16.dp))
                        Text("Prev")
                    }

                    Button(
                        onClick = onFinishPractice,
                        colors = ButtonDefaults.buttonColors(containerColor = AccentEmerald),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("finish_practice_button")
                    ) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Finish & Analyze", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onNextSlide,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
                        modifier = Modifier.testTag("next_practice_slide_button")
                    ) {
                        Text(if (activeSlideIndex == presentation.slides.size - 1) "Finish" else "Next")
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(imageVector = Icons.Default.ArrowForwardIos, contentDescription = "Next Slide", modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Live Presentation Pacing & Time Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Timer, contentDescription = null, tint = PrimaryIndigo, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = timeFormatted,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        // Slide time vs target
                        Surface(
                            color = if (slideElapsedSeconds > currentSlide.estimatedSeconds + 15) AccentAmber.copy(alpha = 0.15f)
                            else PrimaryIndigoContainer,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "Slide: ${slideElapsedSeconds}s / ${currentSlide.estimatedSeconds}s",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (slideElapsedSeconds > currentSlide.estimatedSeconds + 15) AccentAmber
                                    else PrimaryIndigoDark
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    // Progress indicators
                    val overallProgress = (practiceElapsedSeconds.toFloat() / totalTargetSeconds.toFloat()).coerceIn(0f, 1f)
                    LinearProgressIndicator(
                        progress = { overallProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = PrimaryIndigo
                    )
                }
            }

            // Real-Time Coaching Banner
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = if (speechState.isReadingDetected) AccentAmber.copy(alpha = 0.15f)
                else AccentEmerald.copy(alpha = 0.12f),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (speechState.isReadingDetected) AccentAmber else AccentEmerald.copy(alpha = 0.4f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (speechState.isReadingDetected) Icons.Default.Visibility else Icons.Default.Psychology,
                        contentDescription = "Coach Prompt",
                        tint = if (speechState.isReadingDetected) AccentAmber else AccentEmerald,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (speechState.isReadingDetected) "COACH OBSERVATION" else "LIVE COACHING",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (speechState.isReadingDetected) AccentAmber else AccentEmerald
                            )
                        )
                        Text(
                            text = speechState.currentCoachingTip,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                }
            }

            // Optional Visual Presence Coaching Notice (when camera enabled)
            if (isCameraCoachingEnabled) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    color = SecondaryVioletContainer.copy(alpha = 0.6f)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.RemoveRedEye, contentDescription = null, tint = SecondaryViolet, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Visual Presence Coach: Maintain eye level with audience. Natural posture detected.",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface)
                        )
                    }
                }
            }

            // Audio Waveform & Status
            AudioWaveform(
                isListening = speechState.isListening,
                amplitude = speechState.currentRmsDb
            )

            // Current Slide View
            SlideCard(
                slide = currentSlide,
                theme = presentation.theme
            )

            // Next Slide Miniature Preview
            if (nextSlide != null) {
                Column {
                    Text(
                        text = "NEXT SLIDE PREVIEW",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 1.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    SlideCard(
                        slide = nextSlide,
                        theme = presentation.theme,
                        isMiniature = true
                    )
                }
            }

            // Speaking Coach Guidance Panel for Current Slide
            SpeakingGuidePanel(
                guide = currentSlide.speakingGuide,
                isPresenterMode = true,
                keyPointsHit = speechState.keyPointsHit
            )

            // Speech Simulation Helper (useful for quick emulator rehearsal)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "REHEARSAL HELPER",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = PrimaryIndigo)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Testing in emulator or noisy room? Click below to deliver a sample speech segment:",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = {
                                onInjectSampleSpeech(
                                    currentSlide.speakingGuide.openingLine + " " +
                                    currentSlide.speakingGuide.whatToSay
                                )
                            },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("Deliver Ideal Guide", style = MaterialTheme.typography.labelSmall)
                        }
                        OutlinedButton(
                            onClick = {
                                onInjectSampleSpeech(
                                    "Um, so yeah, basically like " + currentSlide.bullets.joinToString(", ")
                                )
                            },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("Simulate Fillers & Reading", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
