package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.IdeaVaultEntity
import com.example.data.UserEntity
import com.example.ui.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IdeaVaultView(
    user: UserEntity,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val ideas by viewModel.ideasFlow.collectAsState()
    var showAddModal by remember { mutableStateOf(false) }
    var selectedIdeaForProof by remember { mutableStateOf<IdeaVaultEntity?>(null) }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddModal = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                icon = { Icon(Icons.Default.AddModerator, contentDescription = null) },
                text = { Text("Shield New Idea", fontWeight = FontWeight.Bold) }
            )
        },
        modifier = modifier.fillMaxSize()
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // IP Shield Status Banner
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("ANTI-COPY IP PROTECTION VAULT", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "Every idea submitted here is cryptographically watermarked with your student ID (${user.studentId}) & immutable SHA-256 timestamp. No unauthorized party can copy your concept.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "My Shielded Concepts (${ideas.size})",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(20.dp))
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (ideas.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Lightbulb, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.Gray)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No ideas registered yet.", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.Gray)
                        Text("Tap 'Shield New Idea' to lock your startup or project concept.", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(ideas, key = { it.id }) { idea ->
                        IdeaShieldCard(
                            idea = idea,
                            onProofClick = { selectedIdeaForProof = idea },
                            onDelete = { viewModel.deleteIdea(idea) }
                        )
                    }
                }
            }
        }
    }

    if (showAddModal) {
        AddIdeaModal(
            onDismiss = { showAddModal = false },
            onSubmit = { title, cat, prob, sol, aud ->
                viewModel.protectAndSaveIdea(user.id, user.email, title, cat, prob, sol, aud)
                showAddModal = false
            }
        )
    }

    selectedIdeaForProof?.let { idea ->
        CryptographicProofCertificateDialog(user, idea, onDismiss = { selectedIdeaForProof = null })
    }
}

@Composable
fun IdeaShieldCard(
    idea: IdeaVaultEntity,
    onProofClick: () -> Unit,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(3.dp),
        modifier = Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = idea.category.uppercase(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = idea.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Problem: ${idea.problemStatement}",
                fontSize = 13.sp,
                maxLines = if (expanded) Int.MAX_VALUE else 2,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    Text("Solution:", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                    Text(idea.proposedSolution, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Target Audience: ${idea.targetAudience}", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Patent Score & Watermark Box
            ContainerBox(
                modifier = Modifier.fillMaxWidth().clickable { onProofClick() }
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Verified, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("SHA-256 Fingerprint Shielded", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                        }
                        Text("Patent Readiness: ${idea.patentScore}%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Hash: ${idea.watermarkFingerprint}",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Tap to view official IP Non-Disclosure Proof Certificate", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = dateFormat.format(Date(idea.createdAt)), fontSize = 11.sp, color = Color.Gray)
                TextButton(onClick = { expanded = !expanded }) {
                    Text(if (expanded) "Show Less" else "Expand Full Pitch", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun ContainerBox(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
    ) {
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddIdeaModal(
    onDismiss: () -> Unit,
    onSubmit: (String, String, String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("SaaS") }
    var problem by remember { mutableStateOf("") }
    var solution by remember { mutableStateOf("") }
    var audience by remember { mutableStateOf("") }

    val categories = listOf("SaaS", "AI/ML", "Cybersecurity", "EdTech", "FinTech")
    var catExpanded by remember { mutableStateOf(false) }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .padding(bottom = 30.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Shield, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Register Idea in Anti-Copy IP Shield", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Project / Idea Title") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            Spacer(modifier = Modifier.height(10.dp))

            ExposedDropdownMenuBox(expanded = catExpanded, onExpandedChange = { catExpanded = !catExpanded }) {
                OutlinedTextField(
                    value = category,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Ecosystem Category") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = catExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = catExpanded, onDismissRequest = { catExpanded = false }) {
                    categories.forEach { cat ->
                        DropdownMenuItem(text = { Text(cat) }, onClick = { category = cat; catExpanded = false })
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(value = problem, onValueChange = { problem = it }, label = { Text("Problem Statement") }, modifier = Modifier.fillMaxWidth(), minLines = 2, maxLines = 4)
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(value = solution, onValueChange = { solution = it }, label = { Text("Proposed Secret Solution / Architecture") }, modifier = Modifier.fillMaxWidth(), minLines = 3, maxLines = 5)
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(value = audience, onValueChange = { audience = it }, label = { Text("Target Student / Industry Audience") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = { onSubmit(title, category, problem, solution, audience) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Lock, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cryptographically Encrypt & Save", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun CryptographicProofCertificateDialog(user: UserEntity, idea: IdeaVaultEntity, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.Verified, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(40.dp)) },
        title = { Text("NEXUS IP OWNERSHIP CERTIFICATE", fontWeight = FontWeight.Bold, fontSize = 16.sp, textAlign = TextAlign.Center) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("This digital certificate validates absolute authorship and non-disclosure priority under the Nexus Student Intellectual Property Shield.", fontSize = 12.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Divider()
                Spacer(modifier = Modifier.height(8.dp))
                ProofRow("Author:", user.fullName)
                ProofRow("Student ID:", user.studentId)
                ProofRow("University:", user.universityName)
                ProofRow("Concept:", idea.title)
                ProofRow("Category:", idea.category)
                ProofRow("Patent Score:", "${idea.patentScore}% Algorithmic Readiness")
                ProofRow("Timestamp:", SimpleDateFormat("yyyy-MM-dd HH:mm:ss z", Locale.getDefault()).format(Date(idea.createdAt)))
                Spacer(modifier = Modifier.height(8.dp))
                Text("Immutable Watermark Hash:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(6.dp), modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
                    Text(idea.watermarkFingerprint, fontFamily = FontFamily.Monospace, fontSize = 12.sp, modifier = Modifier.padding(8.dp), color = MaterialTheme.colorScheme.primary)
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                Text("Acknowledge & Close")
            }
        }
    )
}

@Composable
fun ProofRow(label: String, valText: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
        Text(valText, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}
