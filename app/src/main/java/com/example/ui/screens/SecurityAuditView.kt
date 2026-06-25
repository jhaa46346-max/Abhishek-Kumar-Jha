package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SecurityLogEntity
import com.example.data.SecurityUtils
import com.example.data.UserEntity
import com.example.ui.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SecurityAuditView(
    user: UserEntity,
    viewModel: MainViewModel,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val logs by viewModel.securityLogsFlow.collectAsState()
    val daysRemaining = remember(user.lastLoginTimestamp) {
        SecurityUtils.getDaysRemaining(user.lastLoginTimestamp)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Student Identity & Shield Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(36.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(user.fullName, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("${user.universityName} • ID: ${user.studentId}", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(user.email, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Divider()
                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text("30-DAY MANDATORY RE-AUTH", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Text("$daysRemaining Days Valid", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                        Text("Master password & 2FA OTP verification required upon expiry", fontSize = 10.sp, color = Color.Gray)
                    }
                    Button(
                        onClick = onLogoutClick,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Sign Out", fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Anti-Theft Specs Banner
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.EnhancedEncryption, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Complexity & Anti-Copy Guarantee", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "• SHA-256 Salted Password Hashing\n• Cryptographic Watermarking on pitches\n• 3-Strike Failed Attempt IP Lockout\n• Sandbox Simulated SMS 2FA Tokens",
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Cryptographic Audit Trail",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (logs.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No security events recorded yet.", fontSize = 13.sp, color = Color.Gray)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 40.dp)
            ) {
                items(logs, key = { it.id }) { log ->
                    AuditLogItem(log)
                }
            }
        }
    }
}

@Composable
fun AuditLogItem(log: SecurityLogEntity) {
    val dateFormat = remember { SimpleDateFormat("HH:mm:ss • MMM dd", Locale.getDefault()) }
    val icon = when {
        log.actionType.contains("SUCCESS") -> Icons.Default.CheckCircle
        log.actionType.contains("FAILED") || log.actionType.contains("BLOCKED") || log.actionType.contains("LOCKED") -> Icons.Default.Error
        log.actionType.contains("IDEA") -> Icons.Default.Shield
        else -> Icons.Default.Info
    }
    val tint = when {
        log.actionType.contains("SUCCESS") -> MaterialTheme.colorScheme.secondary
        log.actionType.contains("FAILED") || log.actionType.contains("BLOCKED") || log.actionType.contains("LOCKED") -> MaterialTheme.colorScheme.error
        log.actionType.contains("IDEA") -> MaterialTheme.colorScheme.primary
        else -> Color.Gray
    }

    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(log.actionType, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = tint)
                    Text(dateFormat.format(Date(log.timestamp)), fontSize = 10.sp, color = Color.Gray)
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(log.details, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                Text("IP: ${log.ipAddress}", fontSize = 9.sp, fontFamily = FontFamily.Monospace, color = Color.Gray)
            }
        }
    }
}
