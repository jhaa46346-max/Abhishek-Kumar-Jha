package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.UserEntity

data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: String,
    val icon: ImageVector,
    val color: Color,
    val isUnread: Boolean = true
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationMenuDialog(
    onDismiss: () -> Unit
) {
    val notifications = remember {
        listOf(
            NotificationItem(
                "1",
                "AI Models Synced",
                "ChatGPT (GPT-4o/o1), Claude 3.5 Sonnet & DeepSeek R1 are active and ready for engineering & coding queries.",
                "Just now",
                Icons.Default.AutoAwesome,
                Color(0xFF10A37F)
            ),
            NotificationItem(
                "2",
                "Security Shield Active",
                "30-Day IP Protection & Student Sandbox Firewall enabled. Zero unauthorized trackers detected.",
                "10m ago",
                Icons.Default.Shield,
                Color(0xFF4285F4)
            ),
            NotificationItem(
                "3",
                "Bachelor's Degree Topics Added",
                "Complete solution portal added for MIT OCW, NPTEL IIT curriculum, Paul's Math Notes & CircuitLab.",
                "1h ago",
                Icons.Default.School,
                Color(0xFFE53E3E)
            ),
            NotificationItem(
                "4",
                "IDE Compiler Ready",
                "Replit Agent IDE, LeetCode & GitHub integration are operating at optimal latency.",
                "3h ago",
                Icons.Default.Code,
                Color(0xFFFFA116)
            )
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Notifications & Alerts", fontWeight = FontWeight.Bold)
                }
                Badge(containerColor = MaterialTheme.colorScheme.error) {
                    Text("4 NEW")
                }
            }
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp)
            ) {
                items(notifications) { notif ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(notif.color.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(notif.icon, contentDescription = null, tint = notif.color, modifier = Modifier.size(22.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(notif.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                    Text(notif.timestamp, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    notif.message,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Dismiss All")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainMenuSheet(
    user: UserEntity,
    onDismiss: () -> Unit,
    onLogoutClick: () -> Unit
) {
    var studentName by remember { mutableStateOf(user.fullName) }
    var university by remember { mutableStateOf("Global Tech Institute") }
    var major by remember { mutableStateOf("B.S. Computer Science & AI") }
    var yearOfStudy by remember { mutableStateOf("Junior (3rd Year)") }

    var isAiSandboxEnabled by remember { mutableStateOf(true) }
    var isExamLockdown by remember { mutableStateOf(false) }
    var isVpnShield by remember { mutableStateOf(true) }
    var isAutoCitation by remember { mutableStateOf(true) }

    var saveConfirmation by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .fillMaxWidth()
                .verticalScroll(androidx.compose.foundation.rememberScrollState())
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Tune, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Nexus Hub Customization Menu", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text("Edit anything & change as per your requirement", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            // Section 1: Edit Student Profile & Degree
            Text("🎓 Bachelor's Degree & Student Profile", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = studentName,
                onValueChange = { studentName = it },
                label = { Text("Student Name") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = university,
                onValueChange = { university = it },
                label = { Text("University / Institution") },
                leadingIcon = { Icon(Icons.Default.AccountBalance, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = major,
                onValueChange = { major = it },
                label = { Text("Degree Major & Specialization") },
                leadingIcon = { Icon(Icons.Default.School, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = yearOfStudy,
                onValueChange = { yearOfStudy = it },
                label = { Text("Academic Level / Year") },
                leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )

            if (saveConfirmation) {
                Spacer(modifier = Modifier.height(8.dp))
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                    Row(modifier = Modifier.padding(12.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Profile & degree preferences updated dynamically!", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = { saveConfirmation = true },
                modifier = Modifier.align(Alignment.End)
            ) {
                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save Changes")
            }

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(20.dp))

            // Section 2: Security & Privacy Toggles
            Text("🛡️ Active Security & Environment Toggles", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(12.dp))

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Student Web Sandbox", fontWeight = FontWeight.Bold)
                                Text("Isolate web links & block telemetry scripts", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Switch(checked = isAiSandboxEnabled, onCheckedChange = { isAiSandboxEnabled = it })
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFFE53E3E))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Strict Exam Lockdown Mode", fontWeight = FontWeight.Bold)
                                Text("Prevent clipboard leakage & unauthorized tabs", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Switch(checked = isExamLockdown, onCheckedChange = { isExamLockdown = it })
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.VpnKey, contentDescription = null, tint = Color(0xFF10A37F))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Academic VPN Shield", fontWeight = FontWeight.Bold)
                                Text("Simulated secure tunnel for university journals", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Switch(checked = isVpnShield, onCheckedChange = { isVpnShield = it })
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.MenuBook, contentDescription = null, tint = Color(0xFF4285F4))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Auto Citation Generator", fontWeight = FontWeight.Bold)
                                Text("Format AI & paper answers in IEEE / APA 7th", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Switch(checked = isAutoCitation, onCheckedChange = { isAutoCitation = it })
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(20.dp))

            // Section 3: Quick System Actions
            Text("⚙️ Workspace Management", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = { /* Reset */ },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Clear AI Cache")
                }
                OutlinedButton(
                    onClick = onLogoutClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Log Out")
                }
            }

            Spacer(modifier = Modifier.height(36.dp))
        }
    }
}
