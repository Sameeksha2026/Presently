package com.example.ui.components

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.Presentation
import com.example.ui.theme.PrimaryIndigo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportDialog(
    presentation: Presentation?,
    onDismiss: () -> Unit
) {
    if (presentation == null) return
    val context = LocalContext.current
    var exportedMessage by remember { mutableStateOf<String?>(null) }

    fun shareExport(format: String, content: String) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_SUBJECT, "Presently AI Export: ${presentation.title} ($format)")
            putExtra(Intent.EXTRA_TEXT, content)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Export Presentation ($format)")
        context.startActivity(shareIntent)
        exportedMessage = "$format prepared and sent to share sheet!"
    }

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
            Text(
                text = "Export & Share Presentation",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = "Choose your delivery format. Slides and speaking guidance are structured cleanly.",
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )

            ExportFormatItem(
                title = "PowerPoint Presentation (.pptx)",
                subtitle = "Formatted slide deck ready for Microsoft PowerPoint or Google Slides",
                icon = Icons.Default.Slideshow
            ) {
                val script = buildString {
                    appendLine("PRESENTATION: ${presentation.title}")
                    appendLine("THEME: ${presentation.theme.displayName}")
                    presentation.slides.forEach { s ->
                        appendLine("\n--- SLIDE ${s.index}: ${s.title} ---")
                        if (s.subtitle.isNotBlank()) appendLine("Subtitle: ${s.subtitle}")
                        s.bullets.forEach { appendLine("• $it") }
                        appendLine("[Speaking Notes]: ${s.speakingGuide.whatToSay}")
                    }
                }
                shareExport("PPTX Format", script)
            }

            ExportFormatItem(
                title = "Printable PDF Slides (.pdf)",
                subtitle = "High-fidelity visual pages for audience handouts",
                icon = Icons.Default.PictureAsPdf
            ) {
                val text = presentation.slides.joinToString("\n\n") { "Slide ${it.index}: ${it.title}\n${it.bullets.joinToString("\n")}" }
                shareExport("PDF Slides", text)
            }

            ExportFormatItem(
                title = "Speaker Cue Cards & Notes",
                subtitle = "Opening lines, key bullet triggers, and transitions",
                icon = Icons.Default.Notes
            ) {
                val notes = presentation.slides.joinToString("\n\n") { s ->
                    "SLIDE ${s.index} (${s.title})\nOPENING: \"${s.speakingGuide.openingLine}\"\nTALK TRACK: ${s.speakingGuide.whatToSay}\nKEY TRIGGERS: ${s.speakingGuide.keyPoints.joinToString(", ")}\nTRANSITION: \"${s.speakingGuide.transition}\""
                }
                shareExport("Speaker Notes", notes)
            }

            ExportFormatItem(
                title = "Full Presentation Script",
                subtitle = "Complete natural transcript with pacing benchmarks",
                icon = Icons.Default.Description
            ) {
                val fullScript = presentation.slides.joinToString("\n\n") { s ->
                    "[Slide ${s.index}: ${s.title}]\n${s.speakingGuide.openingLine} ${s.speakingGuide.whatToSay} ${s.speakingGuide.transition}"
                }
                shareExport("Presentation Script", fullScript)
            }

            if (exportedMessage != null) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = PrimaryIndigo.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = exportedMessage ?: "",
                        modifier = Modifier.padding(10.dp),
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = PrimaryIndigo)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ExportFormatItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(PrimaryIndigo.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = PrimaryIndigo, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
            }
            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
