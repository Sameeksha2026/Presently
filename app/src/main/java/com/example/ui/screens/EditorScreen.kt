package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.model.*
import com.example.ui.components.SlideCard
import com.example.ui.components.SpeakingGuidePanel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    presentation: Presentation?,
    activeSlideIndex: Int,
    isImprovingSlide: Boolean,
    showImproveSheet: Boolean,
    onSelectSlide: (Int) -> Unit,
    onAddSlide: () -> Unit,
    onDeleteSlide: () -> Unit,
    onMoveSlide: (Boolean) -> Unit,
    onSelectTheme: (SlideThemeType) -> Unit,
    onOpenImproveSheet: () -> Unit,
    onCloseImproveSheet: () -> Unit,
    onImproveSlideAction: (String) -> Unit,
    onUpdateSlideContent: (String, String, List<String>) -> Unit,
    onStartPractice: () -> Unit,
    onExport: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (presentation == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No active presentation")
        }
        return
    }

    val currentSlide = presentation.slides.getOrNull(activeSlideIndex)
        ?: presentation.slides.firstOrNull()

    var showEditDialog by remember { mutableStateOf(false) }
    var editTitle by remember { mutableStateOf("") }
    var editSubtitle by remember { mutableStateOf("") }
    var editBulletsText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = presentation.title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            maxLines = 1
                        )
                        Text(
                            text = "Slide ${activeSlideIndex + 1} of ${presentation.slides.size} • ${presentation.theme.displayName}",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onExport) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = "Export")
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
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onOpenImproveSheet,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("improve_slide_button"),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, PrimaryIndigo)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoFixHigh,
                            contentDescription = null,
                            tint = PrimaryIndigo,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Improve Slide",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = PrimaryIndigo
                            )
                        )
                    }

                    Button(
                        onClick = onStartPractice,
                        modifier = Modifier
                            .weight(1.3f)
                            .height(48.dp)
                            .testTag("present_practice_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Present & Practice",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 24.dp)
        ) {
            // Horizontal Slide Thumbnail Strip
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp)
                ) {
                    itemsIndexed(presentation.slides) { idx, slide ->
                        val isSelected = idx == activeSlideIndex
                        Card(
                            modifier = Modifier
                                .width(120.dp)
                                .height(76.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onSelectSlide(idx) },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) PrimaryIndigoContainer else MaterialTheme.colorScheme.surface
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                if (isSelected) 2.dp else 1.dp,
                                if (isSelected) PrimaryIndigo else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "0${slide.index}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) PrimaryIndigoDark else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                                Text(
                                    text = slide.title,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    maxLines = 2
                                )
                            }
                        }
                    }

                    // Add Slide button chip
                    item {
                        Surface(
                            modifier = Modifier
                                .width(60.dp)
                                .height(76.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onAddSlide() },
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Slide", tint = PrimaryIndigo)
                            }
                        }
                    }
                }
            }

            // Slide Control Toolbar (Reorder, Edit, Delete, Theme)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(
                            onClick = { onMoveSlide(true) },
                            enabled = activeSlideIndex > 0
                        ) {
                            Icon(imageVector = Icons.Default.ArrowUpward, contentDescription = "Move Up")
                        }
                        IconButton(
                            onClick = { onMoveSlide(false) },
                            enabled = activeSlideIndex < presentation.slides.size - 1
                        ) {
                            Icon(imageVector = Icons.Default.ArrowDownward, contentDescription = "Move Down")
                        }
                        IconButton(
                            onClick = {
                                if (currentSlide != null) {
                                    editTitle = currentSlide.title
                                    editSubtitle = currentSlide.subtitle
                                    editBulletsText = currentSlide.bullets.joinToString("\n")
                                    showEditDialog = true
                                }
                            }
                        ) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Text")
                        }
                        IconButton(
                            onClick = onDeleteSlide,
                            enabled = presentation.slides.size > 1
                        ) {
                            Icon(imageVector = Icons.Default.DeleteOutline, contentDescription = "Delete Slide", tint = AccentRose)
                        }
                    }

                    // Theme selector dropdown button
                    var showThemeMenu by remember { mutableStateOf(false) }
                    Box {
                        OutlinedButton(
                            onClick = { showThemeMenu = true },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Palette, contentDescription = "Theme", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = presentation.theme.displayName.split(" ").first(),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                        DropdownMenu(
                            expanded = showThemeMenu,
                            onDismissRequest = { showThemeMenu = false }
                        ) {
                            SlideThemeType.entries.forEach { theme ->
                                DropdownMenuItem(
                                    text = { Text(theme.displayName) },
                                    onClick = {
                                        onSelectTheme(theme)
                                        showThemeMenu = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Current Slide View
            if (currentSlide != null) {
                item {
                    if (isImprovingSlide) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp),
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    CircularProgressIndicator(color = PrimaryIndigo)
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "AI is refining this slide...",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                            }
                        }
                    } else {
                        SlideCard(
                            slide = currentSlide,
                            theme = presentation.theme
                        )
                    }
                }

                // Speaking Guide for Current Slide
                item {
                    SpeakingGuidePanel(
                        guide = currentSlide.speakingGuide
                    )
                }
            }
        }
    }

    // "Improve this slide" Bottom Sheet
    if (showImproveSheet && currentSlide != null) {
        ModalBottomSheet(
            onDismissRequest = onCloseImproveSheet,
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = PrimaryIndigo,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Improve Slide ${activeSlideIndex + 1}",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
                Text(
                    text = "Select how you'd like Presently AI to polish this slide:",
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )

                val improvementOptions = listOf(
                    "Make it simpler" to "Strip away jargon and keep focus on the single core thesis.",
                    "Make it more visual" to "Transform text bullets into a structured flowchart or comparison.",
                    "Reduce text" to "Trim word count by 50% so slides support your voice, not replace it.",
                    "Make it more professional" to "Elevate tone for executive leadership or formal conferences.",
                    "Add an example" to "Introduce an everyday concrete illustration for faster audience recall.",
                    "Add a diagram" to "Generate a relational process or cycle diagram for this slide."
                )

                improvementOptions.forEach { (title, subtitle) ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onImproveSlideAction(title) },
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = subtitle,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = PrimaryIndigo,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Edit Slide Text Dialog
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Slide ${activeSlideIndex + 1}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = editTitle,
                        onValueChange = { editTitle = it },
                        label = { Text("Slide Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editSubtitle,
                        onValueChange = { editSubtitle = it },
                        label = { Text("Subtitle") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editBulletsText,
                        onValueChange = { editBulletsText = it },
                        label = { Text("Bullet Points (one per line)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val bullets = editBulletsText.lines().filter { it.isNotBlank() }
                        onUpdateSlideContent(editTitle, editSubtitle, bullets)
                        showEditDialog = false
                    }
                ) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
