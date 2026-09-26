package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.SlideThemes

data class ThemeColorConfig(
    val bg: Color,
    val cardBg: Color,
    val primaryText: Color,
    val accent: Color,
    val bodyText: Color,
    val border: Color,
    val gradient: Brush? = null
)

fun getThemeColors(theme: SlideThemeType): ThemeColorConfig {
    return when (theme) {
        SlideThemeType.MODERN_ACADEMIC -> ThemeColorConfig(
            bg = SlideThemes.AcademicBg,
            cardBg = SlideThemes.AcademicCard,
            primaryText = SlideThemes.AcademicPrimary,
            accent = SlideThemes.AcademicAccent,
            bodyText = SlideThemes.AcademicText,
            border = SlideThemes.AcademicBorder
        )
        SlideThemeType.MINIMAL_PROFESSIONAL -> ThemeColorConfig(
            bg = SlideThemes.MinimalBg,
            cardBg = SlideThemes.MinimalCard,
            primaryText = SlideThemes.MinimalPrimary,
            accent = SlideThemes.MinimalAccent,
            bodyText = SlideThemes.MinimalText,
            border = SlideThemes.MinimalBorder
        )
        SlideThemeType.BOLD_STARTUP -> ThemeColorConfig(
            bg = SlideThemes.StartupBg,
            cardBg = SlideThemes.StartupCard,
            primaryText = Color.White,
            accent = SlideThemes.StartupPrimary,
            bodyText = Color(0xFFCBD5E1),
            border = SlideThemes.StartupBorder,
            gradient = Brush.linearGradient(listOf(Color(0xFF1E1B4B), Color(0xFF0F172A)))
        )
        SlideThemeType.DARK_TECH -> ThemeColorConfig(
            bg = SlideThemes.TechBg,
            cardBg = SlideThemes.TechCard,
            primaryText = SlideThemes.TechPrimary,
            accent = SlideThemes.TechAccent,
            bodyText = SlideThemes.TechText,
            border = SlideThemes.TechBorder,
            gradient = Brush.verticalGradient(listOf(Color(0xFF090D16), Color(0xFF111827)))
        )
        SlideThemeType.CLEAN_EDUCATION -> ThemeColorConfig(
            bg = SlideThemes.EducationBg,
            cardBg = SlideThemes.EducationCard,
            primaryText = SlideThemes.EducationPrimary,
            accent = SlideThemes.EducationAccent,
            bodyText = SlideThemes.EducationText,
            border = SlideThemes.EducationBorder
        )
        SlideThemeType.ELEGANT_EDITORIAL -> ThemeColorConfig(
            bg = SlideThemes.EditorialBg,
            cardBg = SlideThemes.EditorialCard,
            primaryText = SlideThemes.EditorialPrimary,
            accent = SlideThemes.EditorialAccent,
            bodyText = SlideThemes.EditorialText,
            border = SlideThemes.EditorialBorder
        )
    }
}

@Composable
fun SlideCard(
    slide: Slide,
    theme: SlideThemeType,
    modifier: Modifier = Modifier,
    isMiniature: Boolean = false
) {
    val colors = getThemeColors(theme)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.5.dp, colors.border, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = colors.cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isMiniature) 2.dp else 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (colors.gradient != null) Modifier.background(colors.gradient)
                    else Modifier.background(colors.cardBg)
                )
                .padding(if (isMiniature) 10.dp else 20.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Header: Slide index & Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = colors.accent.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "SLIDE ${slide.index}",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = colors.accent,
                                letterSpacing = 1.sp
                            )
                        )
                    }

                    if (!isMiniature && slide.estimatedSeconds > 0) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = "Estimated Time",
                                tint = colors.bodyText.copy(alpha = 0.7f),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${slide.estimatedSeconds}s",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = colors.bodyText.copy(alpha = 0.7f)
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(if (isMiniature) 6.dp else 14.dp))

                // Title & Subtitle
                Text(
                    text = slide.title,
                    style = if (isMiniature) MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    else MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = colors.primaryText
                    )
                )

                if (slide.subtitle.isNotBlank() && !isMiniature) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = slide.subtitle,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = colors.accent,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }

                Spacer(modifier = Modifier.height(if (isMiniature) 8.dp else 16.dp))

                // Visual Diagram (if present)
                if (slide.visualType != VisualType.NONE && !isMiniature) {
                    DiagramContainer(
                        visualType = slide.visualType,
                        data = slide.visualData,
                        colors = colors
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Bullets
                if (slide.bullets.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(if (isMiniature) 4.dp else 8.dp)) {
                        slide.bullets.take(if (isMiniature) 2 else 4).forEach { bullet ->
                            Row(
                                verticalAlignment = Alignment.Top,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .padding(top = if (isMiniature) 4.dp else 6.dp)
                                        .size(if (isMiniature) 4.dp else 6.dp)
                                        .clip(CircleShape)
                                        .background(colors.accent)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = bullet.removePrefix("•").trim(),
                                    style = if (isMiniature) MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp)
                                    else MaterialTheme.typography.bodyMedium.copy(
                                        color = colors.bodyText,
                                        lineHeight = 20.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DiagramContainer(
    visualType: VisualType,
    data: VisualData,
    colors: ThemeColorConfig
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, colors.border.copy(alpha = 0.6f), RoundedCornerShape(12.dp)),
        color = colors.bg.copy(alpha = 0.5f)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            when (visualType) {
                VisualType.FLOWCHART -> {
                    Text(
                        text = "PROCESS FLOW",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = colors.accent,
                            letterSpacing = 1.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        data.steps.take(3).forEachIndexed { index, step ->
                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                color = colors.cardBg,
                                border = androidx.compose.foundation.BorderStroke(1.dp, colors.accent.copy(alpha = 0.3f))
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(
                                        text = "0${step.number}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = colors.accent,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Text(
                                        text = step.title,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            color = colors.primaryText
                                        ),
                                        maxLines = 1
                                    )
                                    Text(
                                        text = step.description,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 10.sp,
                                            color = colors.bodyText
                                        ),
                                        maxLines = 2
                                    )
                                }
                            }
                            if (index < data.steps.take(3).size - 1) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Next step",
                                    tint = colors.accent,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
                VisualType.COMPARISON -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Left
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            color = colors.cardBg.copy(alpha = 0.7f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, colors.border)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    text = data.comparisonLeftTitle.ifBlank { "Traditional" },
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = colors.bodyText
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                data.comparisonLeft.take(3).forEach { item ->
                                    Text(
                                        text = "✕ $item",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 11.sp,
                                            color = colors.bodyText.copy(alpha = 0.8f)
                                        ),
                                        maxLines = 2
                                    )
                                }
                            }
                        }
                        // Right
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            color = colors.accent.copy(alpha = 0.1f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, colors.accent.copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    text = data.comparisonRightTitle.ifBlank { "Presently AI" },
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = colors.accent
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                data.comparisonRight.take(3).forEach { item ->
                                    Text(
                                        text = "✓ $item",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = colors.primaryText
                                        ),
                                        maxLines = 2
                                    )
                                }
                            }
                        }
                    }
                }
                VisualType.METRIC_CHART -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        data.metrics.take(3).forEach { metric ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = metric.value,
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = colors.accent
                                    )
                                )
                                Text(
                                    text = metric.label,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Medium,
                                        color = colors.primaryText,
                                        textAlign = TextAlign.Center
                                    ),
                                    maxLines = 2
                                )
                            }
                        }
                    }
                }
                VisualType.TIMELINE -> {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        data.timelineEvents.take(3).forEach { ev ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = colors.accent,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = ev.phase,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = ev.title,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            color = colors.primaryText
                                        )
                                    )
                                    Text(
                                        text = ev.description,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 10.sp,
                                            color = colors.bodyText
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
                VisualType.CYCLE -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        data.cycleNodes.take(4).forEachIndexed { i, node ->
                            Surface(
                                shape = CircleShape,
                                color = colors.accent.copy(alpha = 0.15f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, colors.accent.copy(alpha = 0.4f)),
                                modifier = Modifier.size(54.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = node,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = colors.primaryText,
                                            textAlign = TextAlign.Center
                                        ),
                                        modifier = Modifier.padding(4.dp),
                                        maxLines = 2
                                    )
                                }
                            }
                            if (i < data.cycleNodes.take(4).size - 1) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Loop",
                                    tint = colors.accent,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
                else -> {}
            }
        }
    }
}
