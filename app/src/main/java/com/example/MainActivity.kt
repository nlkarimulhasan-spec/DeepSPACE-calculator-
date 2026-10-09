package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.ui.AboutScreen
import com.example.ui.CalculatorScreen
import com.example.ui.HistoryScreen
import com.example.ui.OwnerScreen
import com.example.ui.SettingsScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.CalculatorViewModel

sealed class Screen {
    object Calculator : Screen()
    object History : Screen()
    object Settings : Screen()
    object Owner : Screen()
    object About : Screen()
}

class MainActivity : ComponentActivity() {
    private val viewModel: CalculatorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeMode by viewModel.themeMode.collectAsState()
            val accentColor by viewModel.accentColor.collectAsState()

            MyApplicationTheme(themeMode = themeMode, accentColor = accentColor) {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    var currentScreen by remember { mutableStateOf<Screen>(Screen.Calculator) }

                    when (currentScreen) {
                        is Screen.Calculator -> {
                            CalculatorScreen(
                                viewModel = viewModel,
                                onNavigateToHistory = { currentScreen = Screen.History },
                                onNavigateToSettings = { currentScreen = Screen.Settings },
                                onNavigateToOwner = { currentScreen = Screen.Owner },
                                onNavigateToAbout = { currentScreen = Screen.About }
                            )
                        }
                        is Screen.History -> {
                            BackHandler { currentScreen = Screen.Calculator }
                            HistoryScreen(
                                viewModel = viewModel,
                                onBack = { currentScreen = Screen.Calculator }
                            )
                        }
                        is Screen.Settings -> {
                            BackHandler { currentScreen = Screen.Calculator }
                            SettingsScreen(
                                viewModel = viewModel,
                                onBack = { currentScreen = Screen.Calculator }
                            )
                        }
                        is Screen.Owner -> {
                            BackHandler { currentScreen = Screen.Calculator }
                            OwnerScreen(
                                onBack = { currentScreen = Screen.Calculator }
                            )
                        }
                        is Screen.About -> {
                            BackHandler { currentScreen = Screen.Calculator }
                            AboutScreen(
                                onBack = { currentScreen = Screen.Calculator }
                            )
                        }
                    }
                }
            }
        }
    }
}
