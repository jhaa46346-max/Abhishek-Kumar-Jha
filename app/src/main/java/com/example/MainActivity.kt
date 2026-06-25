package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.data.UserEntity
import com.example.ui.AuthUiState
import com.example.ui.AuthViewModel
import com.example.ui.MainViewModel
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.CodingHubView
import com.example.ui.screens.IdeaVaultView
import com.example.ui.screens.OmniPortalView
import com.example.ui.screens.SecurityAuditView
import com.example.ui.screens.StudyToolsView
import com.example.ui.screens.MainMenuSheet
import com.example.ui.screens.NotificationMenuDialog
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private val authViewModel by viewModels<AuthViewModel>()
    private val mainViewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val authState by authViewModel.uiState.collectAsState()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when (val state = authState) {
                        is AuthUiState.Authenticated -> {
                            LaunchedEffect(state.user.id) {
                                mainViewModel.setUserId(state.user.id)
                            }
                            MainStudentWorkspace(
                                user = state.user,
                                mainViewModel = mainViewModel,
                                onLogout = { authViewModel.signOut() }
                            )
                        }
                        else -> {
                            AuthScreen(authViewModel = authViewModel)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainStudentWorkspace(
    user: UserEntity,
    mainViewModel: MainViewModel,
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    var showNotifs by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }

    if (showNotifs) {
        NotificationMenuDialog(onDismiss = { showNotifs = false })
    }

    if (showMenu) {
        MainMenuSheet(
            user = user,
            onDismiss = { showMenu = false },
            onLogoutClick = {
                showMenu = false
                onLogout()
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nexus Hub Portal", style = MaterialTheme.typography.titleMedium) },
                actions = {
                    IconButton(onClick = { showNotifs = true }) {
                        BadgedBox(badge = { Badge { Text("4") } }) {
                            Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                        }
                    }
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Main Menu")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Hub, contentDescription = "Omni AI Portal") },
                    label = { Text("Omni AI") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.Terminal, contentDescription = "Coding IDE") },
                    label = { Text("IDE Hub") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.Shield, contentDescription = "Idea Vault") },
                    label = { Text("IP Vault") }
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(Icons.Default.School, contentDescription = "Study Planner") },
                    label = { Text("Study Hub") }
                )
                NavigationBarItem(
                    selected = selectedTab == 4,
                    onClick = { selectedTab = 4 },
                    icon = { Icon(Icons.Default.Security, contentDescription = "Security Shield") },
                    label = { Text("30D Shield") }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when (selectedTab) {
                0 -> OmniPortalView(user = user)
                1 -> CodingHubView(user = user)
                2 -> IdeaVaultView(user = user, viewModel = mainViewModel)
                3 -> StudyToolsView(user = user, viewModel = mainViewModel)
                4 -> SecurityAuditView(user = user, viewModel = mainViewModel, onLogoutClick = onLogout)
            }
        }
    }
}
