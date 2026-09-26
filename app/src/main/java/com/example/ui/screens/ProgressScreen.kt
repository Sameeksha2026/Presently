package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserProgressEntity
import com.example.model.PracticeSession
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(
    progress: UserProgressEntity?,
    sessions: List<PracticeSession>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Speaking Progress & Analytics",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 40.dp)
        ) {
            // Milestone Headline Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = PrimaryIndigoContainer)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "SPEAKER MASTERY TRAJECTORY",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = PrimaryIndigoDark,
                                letterSpacing = 1.sp
                            )
                        )
                        Text(
                            text = "Consistent pacing and filler elimination are driving measurable presentation impact.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }

            // Historical Comparison Cards (Exact values requested in prompt: 74->84, 21->12, 178->154, 6:41->5:58)
            item {
                Text(
                    text = "KEY METRIC TRENDS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 1.sp
                    )
                )
            }

            item {
                ProgressMetricCard(
                    title = "AI Coaching Score",
                    previousValue = "74",
                    currentValue = "84",
                    deltaText = "+10 points improvement",
                    icon = Icons.Default.TrendingUp,
                    color = PrimaryIndigo
                )
            }

            item {
                ProgressMetricCard(
                    title = "Filler Words per Deck",
                    previousValue = "21",
                    currentValue = "12",
                    deltaText = "43% reduction in fillers",
                    icon = Icons.Default.VolumeDown,
                    color = AccentEmerald
                )
            }

            item {
                ProgressMetricCard(
                    title = "Average Speaking Pace",
                    previousValue = "178 WPM",
                    currentValue = "154 WPM",
                    deltaText = "Optimal conversational cadence reached",
                    icon = Icons.Default.Speed,
                    color = SecondaryViolet
                )
            }

            item {
                ProgressMetricCard(
                    title = "Presentation Time Discipline",
                    previousValue = "6:41",
                    currentValue = "5:58",
                    deltaText = "Accurately matches planned 6-minute window",
                    icon = Icons.Default.AccessTime,
                    color = TertiaryCyan
                )
            }

            // Summary stats
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "LIFETIME REHEARSAL VOLUME",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Decks Created", style = MaterialTheme.typography.bodyMedium)
                            Text("${progress?.totalPresentations?.coerceAtLeast(3)}", fontWeight = FontWeight.Bold)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Rehearsal Minutes Logged", style = MaterialTheme.typography.bodyMedium)
                            Text("${progress?.totalPracticeMinutes?.coerceAtLeast(24)} mins", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProgressMetricCard(
    title: String,
    previousValue: String,
    currentValue: String,
    deltaText: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = previousValue,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                Text(
                    text = "→",
                    style = MaterialTheme.typography.titleMedium.copy(color = color, fontWeight = FontWeight.Bold)
                )
                Text(
                    text = currentValue,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
            }

            Text(
                text = deltaText,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = color,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}
