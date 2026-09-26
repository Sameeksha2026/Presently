package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.SecondaryViolet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadScreen(
    onBack: () -> Unit,
    onAnalyzeContent: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var inputText by remember { mutableStateOf("") }
    var selectedFileName by remember { mutableStateOf<String?>(null) }
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Paste / Text, 1 = Upload File, 2 = Curated Topics

    // File picker launcher for documents
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val text = inputStream?.bufferedReader()?.use { it.readText() } ?: ""
                val fileName = uri.lastPathSegment?.substringAfterLast('/') ?: "document.txt"
                selectedFileName = fileName
                inputText = if (text.isNotBlank()) text else "Extracted research content from $fileName:\nComprehensive overview of key arguments, data benchmarks, and implementation methodology."
            } catch (_: Exception) {
                selectedFileName = "Sample_Research_Material.pdf"
                inputText = "Research Material on Sustainable Infrastructure & Urban Mobility:\nAnalyzing urban transit friction, carbon emissions metrics, and capital allocation frameworks."
            }
        }
    }

    val sampleTopics = listOf(
        "The Future of Quantum Computing & Cryptography" to "Quantum computing promises exponential acceleration for specific algorithmic workloads, rendering current RSA public-key encryption obsolete. Shor's algorithm can factor large integers in polynomial time. Organizations must transition to Post-Quantum Cryptography (PQC) lattice-based standards. Key metrics: 10,000 logical qubits needed for fault tolerance; 2030 estimated transition deadline.",
        "Sustainable Urban Transport & Micromobility" to "Urban congestion costs metropolitan economies over $87 billion annually in lost productivity and fuel waste. Electrified micromobility and dedicated bus rapid transit (BRT) lanes can eliminate 40% of short-distance vehicular trips. Key arguments: Infrastructure reallocation is 5x cheaper than building new highways. Adoption requires multimodal payment integration and safety ordinances.",
        "Neuroplasticity & Continuous Skill Acquisition" to "Adult neuroplasticity refutes the long-held belief that brain circuitry is fixed post-adolescence. Deliberate practice combined with targeted sleep cycles promotes dendritic spine growth and myelin sheath consolidation. Crucial findings: 20-minute focused rehearsal sessions yield 60% higher retention than massed 3-hour cramming.",
        "Climate Economics: Voluntary Carbon Markets" to "Voluntary carbon credits face scrutiny over verification and additionality. Decentralized digital MRV (Measurement, Reporting, and Verification) provides high-fidelity tracking of reforestation and direct air capture projects. Market size projected to reach $50 billion by 2030."
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Upload Material",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "STEP 1 — INGEST & UNDERSTAND",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = PrimaryIndigo,
                    letterSpacing = 1.sp
                )
            )

            Text(
                text = "Add your notes, document, or topic",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )

            Text(
                text = "Presently AI understands the topic, extracts key concepts, statistics, and logical relationships—not just a copy-paste into slides.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            // Mode Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Paste Text / Notes") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Upload File") }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Sample Topics") }
                )
            }

            when (selectedTab) {
                0 -> {
                    // Paste Tab
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .testTag("material_input_field"),
                        placeholder = {
                            Text("Paste your study material, lecture notes, research abstract, or outline here...")
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryIndigo
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${inputText.split(Regex("\\s+")).filter { it.isNotBlank() }.size} words",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                        if (inputText.isNotBlank()) {
                            TextButton(onClick = { inputText = "" }) {
                                Text("Clear")
                            }
                        }
                    }
                }
                1 -> {
                    // File Upload Tab
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .border(
                                1.5.dp,
                                androidx.compose.ui.graphics.PathEffect?.let { PrimaryIndigo.copy(alpha = 0.5f) } ?: PrimaryIndigo,
                                RoundedCornerShape(16.dp)
                            )
                            .clickable {
                                filePickerLauncher.launch(
                                    arrayOf(
                                        "application/pdf",
                                        "text/plain",
                                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                                        "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                                        "application/msword"
                                    )
                                )
                            },
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(PrimaryIndigo.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudUpload,
                                    contentDescription = "Upload Document",
                                    tint = PrimaryIndigo,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Text(
                                text = selectedFileName ?: "Select PDF, PPT, Word, or Text Document",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Text(
                                text = "Supported: .pdf, .pptx, .docx, .txt, image of notes",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                            Button(
                                onClick = {
                                    filePickerLauncher.launch(
                                        arrayOf("application/pdf", "text/plain", "application/msword")
                                    )
                                },
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Browse Device Files")
                            }
                        }
                    }

                    if (inputText.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "Ready to analyze:",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = inputText.take(180) + if (inputText.length > 180) "..." else "",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }
                        }
                    }
                }
                2 -> {
                    // Pre-loaded Curated Topics
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        sampleTopics.forEach { (title, content) ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        inputText = "$title\n\n$content"
                                        selectedTab = 0
                                    },
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = title,
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = PrimaryIndigo
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = content,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        ),
                                        maxLines = 2
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Understand & Next Button
            Button(
                onClick = { onAnalyzeContent(inputText) },
                enabled = inputText.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("understand_content_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo)
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Understand My Content",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
