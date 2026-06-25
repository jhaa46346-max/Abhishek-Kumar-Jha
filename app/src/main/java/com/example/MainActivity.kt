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
import com.example.ui.screens.IdeaVaultView
import com.example.ui.screens.SecurityAuditView
import com.example.ui.screens.StudyToolsView
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

@Composable
fun MainStudentWorkspace(
    user: UserEntity,
    mainViewModel: MainViewModel,
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Shield, contentDescription = "Idea Vault") },
                    label = { Text("IP Vault") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.School, contentDescription = "Study Planner") },
                    label = { Text("Study Hub") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.Security, contentDescription = "Security Shield") },
                    label = { Text("30D Shield") }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when (selectedTab) {
                0 -> IdeaVaultView(user = user, viewModel = mainViewModel)
                1 -> StudyToolsView(user = user, viewModel = mainViewModel)
                2 -> SecurityAuditView(user = user, viewModel = mainViewModel, onLogoutClick = onLogout)
            }
        }
    }
}
