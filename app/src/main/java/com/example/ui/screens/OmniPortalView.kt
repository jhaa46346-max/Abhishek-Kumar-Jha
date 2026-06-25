package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import kotlinx.coroutines.launch
import java.net.URLEncoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OmniPortalView(
    user: UserEntity,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    
    var activeWebViewUrl by remember { mutableStateOf<String?>(null) }
    var activeWebViewTitle by remember { mutableStateOf("") }

    var isSynthesizing by remember { mutableStateOf(false) }
    var synthesizedResult by remember { mutableStateOf<SynthesizedMultiAnswer?>(null) }
    var activePerspectiveTab by remember { mutableStateOf(0) }

    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    val categories = listOf("All", "AI Assistants", "Engineering & Degree", "Coding & Compilers", "Study & Research", "Math & STEM")

    val filteredPlatforms = remember(selectedCategory) {
        if (selectedCategory == "All") PlatformsRepo.allPlatforms
        else PlatformsRepo.allPlatforms.filter { it.category == selectedCategory }
    }

    fun triggerSynthesis(query: String) {
        if (query.isBlank()) return
        focusManager.clearFocus()
        isSynthesizing = true
        synthesizedResult = null
        scope.launch {
            val res = OmniAiSynthesizer.queryAllSitesSynthesized(query.trim())
            synthesizedResult = res
            isSynthesizing = false
            activePerspectiveTab = 0
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Hero Portal Header
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Hub, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(26.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("NEXUS ALL-IN-ONE STUDENT OMNI-PORTAL", fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "Never switch browser tabs again. Ask one question below to get synthesized multi-AI explanations from DeepSeek, Perplexity, Khan Academy & StackOverflow simultaneously, or launch embedded WebViews directly.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    lineHeight = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Universal AI Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Ask Question / Search Homework Problem...") },
            placeholder = { Text("e.g., Explain Quicksort vs Mergesort in Kotlin") },
            leadingIcon = { Icon(Icons.Default.AutoAwesome, contentDescription = "AI", tint = MaterialTheme.colorScheme.primary) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { triggerSynthesis(searchQuery) }) {
                        Icon(Icons.Default.Send, contentDescription = "Synthesize", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { triggerSynthesis(searchQuery) }),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Synthesizer Result Panel
        if (isSynthesizing) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Synthesizing Omni-AI Multi-Perspective Answer...", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("Gathering DeepSeek logic, academic research & step-by-step math", fontSize = 11.sp, color = Color.Gray)
                }
            }
        }

        synthesizedResult?.let { ans ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp).border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Universal AI Synthesized Answer", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                        }
                        Surface(
                            color = if (ans.isRealAi) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.tertiaryContainer,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                if (ans.isRealAi) "GEMINI 3.5 LIVE" else "STUDENT AI ENGINE",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Tab Switcher for Perspectives
                    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                        SegmentedButton(selected = activePerspectiveTab == 0, onClick = { activePerspectiveTab = 0 }, shape = SegmentedButtonDefaults.itemShape(index = 0, count = 4)) {
                            Text("DeepSeek", fontSize = 10.sp)
                        }
                        SegmentedButton(selected = activePerspectiveTab == 1, onClick = { activePerspectiveTab = 1 }, shape = SegmentedButtonDefaults.itemShape(index = 1, count = 4)) {
                            Text("Perplexity", fontSize = 10.sp)
                        }
                        SegmentedButton(selected = activePerspectiveTab == 2, onClick = { activePerspectiveTab = 2 }, shape = SegmentedButtonDefaults.itemShape(index = 2, count = 4)) {
                            Text("Walkthrough", fontSize = 10.sp)
                        }
                        SegmentedButton(selected = activePerspectiveTab == 3, onClick = { activePerspectiveTab = 3 }, shape = SegmentedButtonDefaults.itemShape(index = 3, count = 4)) {
                            Text("StackOvfl", fontSize = 10.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val activeText = when (activePerspectiveTab) {
                        0 -> ans.deepSeekPerspective
                        1 -> ans.perplexityAcademicSummary
                        2 -> ans.stepByStepSolution
                        else -> ans.stackOverflowAdvice
                    }

                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = activeText,
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            fontFamily = if (activePerspectiveTab == 0) FontFamily.Monospace else FontFamily.Default,
                            modifier = Modifier.padding(12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text("1-Tap Direct In-App Query Openers:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Spacer(modifier = Modifier.height(6.dp))

                    // Direct Web Launch Chips
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val encodedQ = remember(ans.query) { URLEncoder.encode(ans.query, "UTF-8") }
                        
                        QuickLaunchChip("Perplexity AI", Color(0xFF22B8CD)) {
                            activeWebViewTitle = "Perplexity Search: ${ans.query}"
                            activeWebViewUrl = "https://www.perplexity.ai/search?q=$encodedQ"
                        }
                        QuickLaunchChip("ChatGPT", Color(0xFF10A37F)) {
                            activeWebViewTitle = "ChatGPT Query: ${ans.query}"
                            activeWebViewUrl = "https://chatgpt.com/?q=$encodedQ"
                        }
                        QuickLaunchChip("Wolfram", Color(0xFFFF7A00)) {
                            activeWebViewTitle = "WolframAlpha: ${ans.query}"
                            activeWebViewUrl = "https://www.wolframalpha.com/input/?i=$encodedQ"
                        }
                    }
                }
            }
        }

        // Category Pills
        ScrollableTabRow(
            selectedTabIndex = categories.indexOf(selectedCategory),
            edgePadding = 0.dp,
            divider = {},
            containerColor = Color.Transparent,
            modifier = Modifier.fillMaxWidth()
        ) {
            categories.forEach { cat ->
                Tab(
                    selected = selectedCategory == cat,
                    onClick = { selectedCategory = cat },
                    text = { Text(cat, fontSize = 12.sp, fontWeight = if (selectedCategory == cat) FontWeight.Bold else FontWeight.Medium) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Platform Cards List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(filteredPlatforms, key = { it.id }) { platform ->
                PlatformItemCard(
                    platform = platform,
                    currentSearchQuery = searchQuery,
                    onClick = {
                        val finalUrl = if (searchQuery.isNotBlank() && platform.searchUrlTemplate != null) {
                            platform.searchUrlTemplate + URLEncoder.encode(searchQuery.trim(), "UTF-8")
                        } else {
                            platform.url
                        }
                        activeWebViewTitle = "${platform.name} In-App Portal"
                        activeWebViewUrl = finalUrl
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

@Composable
fun QuickLaunchChip(label: String, color: Color, onClick: () -> Unit) {
    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.clickable { onClick() }
    ) {
        Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Launch, contentDescription = null, tint = color, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@Composable
fun PlatformItemCard(
    platform: WebPlatform,
    currentSearchQuery: String,
    onClick: () -> Unit
) {
    val brandColor = Color(platform.badgeColorHex)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(brandColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    when (platform.category) {
                        "AI Assistants" -> Icons.Default.SmartToy
                        "Math & STEM" -> Icons.Default.Calculate
                        "Coding & Compilers" -> Icons.Default.Code
                        "Engineering & Degree" -> Icons.Default.Engineering
                        else -> Icons.Default.School
                    },
                    contentDescription = platform.name,
                    tint = brandColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(platform.name, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(color = brandColor.copy(alpha = 0.1f), shape = RoundedCornerShape(4.dp)) {
                        Text(platform.category, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = brandColor, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(platform.description, fontSize = 12.sp, color = Color.Gray, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onClick,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = brandColor)
            ) {
                Text(if (currentSearchQuery.isNotBlank() && platform.searchUrlTemplate != null) "Query" else "Launch", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(12.dp))
            }
        }
    }
}
