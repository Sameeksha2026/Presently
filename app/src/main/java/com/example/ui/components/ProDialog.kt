package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProDialog(
    onDismiss: () -> Unit
) {
    var isAnnual by remember { mutableStateOf(true) }

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
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(AccentAmber.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = AccentAmber)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Presently AI Pro",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold)
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }

            // Billing Switch
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { isAnnual = true }
                        .padding(4.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = if (isAnnual) PrimaryIndigo else Color.Transparent
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Annual (Save 44%)",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isAnnual) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            text = "₹999 / year",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (isAnnual) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { isAnnual = false }
                        .padding(4.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = if (!isAnnual) PrimaryIndigo else Color.Transparent
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Monthly",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (!isAnnual) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            text = "₹149 / month",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (!isAnnual) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }

            // Features Checklist
            val proFeatures = listOf(
                "Unlimited AI Presentations per month (vs 3 on Free)",
                "Advanced Speech Analysis & Filler Breakdown",
                "Unlimited Live Practice Coaching",
                "Advanced AI Speech Suggestions & Tone Matching",
                "All 6 Premium Design Themes & Visual Generators",
                "Continuous Speaker Progress Tracking & Analytics",
                "AI Presentation Revision after listening",
                "Export to PPTX, PDF, and Speaker Cue Cards"
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                proFeatures.forEach { feat ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = AccentEmerald, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = feat, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium))
                    }
                }
            }

            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo)
            ) {
                Text(
                    text = if (isAnnual) "Upgrade to Pro — ₹999/yr" else "Upgrade to Pro — ₹149/mo",
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "Cancel anytime. Free plan remains fully functional for 3 decks/month.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                ),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
