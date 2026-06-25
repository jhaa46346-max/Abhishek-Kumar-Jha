package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CodingHubView(
    user: UserEntity,
    modifier: Modifier = Modifier
) {
    var activeWebViewUrl by remember { mutableStateOf<String?>(null) }
    var activeWebViewTitle by remember { mutableStateOf("") }

    var selectedLang by remember { mutableStateOf("Kotlin") }
    var codeSnippet by remember { mutableStateOf("fun main() {\n    println(\"Welcome ${user.fullName} to Nexus Code Engine!\")\n    println(\"Status: IP Shield Active & Ready\")\n}") }
    var executionOutput by remember { mutableStateOf<String?>(null) }
    var isRunning by remember { mutableStateOf(false) }

    val languages = listOf("Kotlin", "Python 3", "JavaScript", "C++20")

    val codingPlatforms = remember {
        PlatformsRepo.allPlatforms.filter { it.category == "Coding & Compilers" }
    }

    fun runSimulatedCode() {
        isRunning = true
        executionOutput = null
        // Simulate immediate sandbox execution velocity
        executionOutput = when (selectedLang) {
            "Kotlin" -> "> kotlinc Main.kt -include-runtime -d Main.jar\n> java -jar Main.jar\nWelcome ${user.fullName} to Nexus Code Engine!\nStatus: IP Shield Active & Ready\nBUILD SUCCESSFUL (0.24s)"
            "Python 3" -> "> python3 main.py\nWelcome ${user.fullName} to Nexus Code Engine!\nStatus: IP Shield Active & Ready\nProcess finished with exit code 0"
            "JavaScript" -> "> node index.js\nWelcome ${user.fullName} to Nexus Code Engine!\nStatus: IP Shield Active & Ready"
            else -> "> g++ -O3 main.cpp -o main\n> ./main\nWelcome ${user.fullName} to Nexus Code Engine!\nStatus: IP Shield Active & Ready"
        }
        isRunning = false
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Hero Coding Header
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Terminal, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("NEXUS IN-APP CODING IDE & SANDBOX", fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, color = Color(0xFF38BDF8))
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "Test logic instantly in our multi-language scratchpad below, or launch full Replit IDE, LeetCode, GitHub & HackerRank directly in embedded WebViews.",
                    fontSize = 12.sp,
                    color = Color(0xFFE2E8F0),
                    lineHeight = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick Scratchpad Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Code, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Quick Code Scratchpad", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                    // Language Selector
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        languages.forEach { lang ->
                            Surface(
                                color = if (selectedLang == lang) MaterialTheme.colorScheme.primary else Color.Transparent,
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.clickable {
                                    selectedLang = lang
                                    codeSnippet = when (lang) {
                                        "Python 3" -> "def main():\n    print(\"Welcome ${user.fullName} to Nexus Code Engine!\")\n    print(\"Status: IP Shield Active & Ready\")\n\nif __name__ == '__main__':\n    main()"
                                        "JavaScript" -> "const student = \"${user.fullName}\";\nconsole.log(`Welcome ${user.fullName} to Nexus Code Engine!`);\nconsole.log(\"Status: IP Shield Active & Ready\");"
                                        "C++20" -> "#include <iostream>\n\nint main() {\n    std::cout << \"Welcome ${user.fullName} to Nexus Engine!\\n\";\n    return 0;\n}"
                                        else -> "fun main() {\n    println(\"Welcome ${user.fullName} to Nexus Code Engine!\")\n    println(\"Status: IP Shield Active & Ready\")\n}"
                                    }
                                    executionOutput = null
                                }
                            ) {
                                Text(
                                    text = lang,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedLang == lang) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Editor Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF0F172A))
                        .padding(10.dp)
                ) {
                    BasicTextField(
                        value = codeSnippet,
                        onValueChange = { codeSnippet = it },
                        textStyle = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            color = Color(0xFF38BDF8),
                            lineHeight = 16.sp
                        ),
                        cursorBrush = SolidColor(Color(0xFF38BDF8)),
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Student Sandbox Runner • No extra tab", fontSize = 10.sp, color = Color.Gray)
                    Button(
                        onClick = { runSimulatedCode() },
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Execute Code", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Terminal Output
                executionOutput?.let { out ->
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black)
                            .padding(10.dp)
                    ) {
                        Text(
                            text = out,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = Color(0xFF4ADE80),
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Ecosystem Coding Platforms (${codingPlatforms.size})",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(codingPlatforms, key = { it.id }) { platform ->
                PlatformItemCard(
                    platform = platform,
                    currentSearchQuery = "",
                    onClick = {
                        activeWebViewTitle = "${platform.name} In-App Portal"
                        activeWebViewUrl = platform.url
                    }
                )
            }
        }
    }

    activeWebViewUrl?.let { url ->
        InAppWebViewDialog(
            url = url,
            title = activeWebViewTitle,
            onDismiss = { activeWebViewUrl = null }
        )
    }
}
