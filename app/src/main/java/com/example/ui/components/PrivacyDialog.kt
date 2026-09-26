package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.AccentRose
import com.example.ui.theme.PrimaryIndigo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyDialog(
    onDeletePresentations: () -> Unit,
    onDeleteRecordings: () -> Unit,
    onDismiss: () -> Unit
) {
    var confirmedPresentations by remember { mutableStateOf(false) }
    var confirmedRecordings by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = PrimaryIndigo)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Privacy & Data Sovereignty",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }

            Text(
                text = "Presently AI treats your speech and documents with strict confidentiality.",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
            )

            val privacyPillars = listOf(
                "Microphone Audio: Audio is streamed exclusively for live speech recognition and acoustic analysis. Raw audio files are NOT permanently saved on remote servers.",
                "Presentation Documents: Extracted texts and slides reside in your private device database and are never shared publicly or used to train third-party public models.",
                "Optional Visual Presence: When camera assistance is active, frame telemetry stays local for posture/eye-line feedback."
            )

            privacyPillars.forEach { pillar ->
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = pillar,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            Text(
                text = "USER DATA MANAGEMENT",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = AccentRose)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick = {
                        onDeletePresentations()
                        confirmedPresentations = true
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentRose)
                ) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (confirmedPresentations) "Decks Deleted" else "Delete All Decks")
                }

                OutlinedButton(
                    onClick = {
                        onDeleteRecordings()
                        confirmedRecordings = true
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentRose)
                ) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (confirmedRecordings) "Transcripts Cleared" else "Clear Transcripts")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
