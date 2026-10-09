package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.CalculatorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: CalculatorViewModel,
    onBack: () -> Unit
) {
    val themeMode by viewModel.themeMode.collectAsState()
    val accentColor by viewModel.accentColor.collectAsState()
    val hapticEnabled by viewModel.hapticEnabled.collectAsState()
    val animationEnabled by viewModel.animationEnabled.collectAsState()
    val largeText by viewModel.largeTextEnabled.collectAsState()
    val precision by viewModel.decimalPrecision.collectAsState()
    val thousands by viewModel.thousandsSeparator.collectAsState()
    val trailing by viewModel.trailingZeros.collectAsState()
    val angleMode by viewModel.angleMode.collectAsState()
    val soundEnabled by viewModel.soundEnabled.collectAsState()
    val historyEnabled by viewModel.historyEnabled.collectAsState()

    var showClearHistoryDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("back_button")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Appearance Section
            SettingsSectionHeader("Appearance")
            
            SettingsDropdownCard(
                title = "Theme Mode",
                subtitle = "Choose light, dark, or system default theme",
                options = listOf("light" to "Light Mode", "dark" to "Dark Mode", "system" to "System Default"),
                selectedOption = themeMode,
                onSelected = { viewModel.setThemeMode(it) }
            )

            SettingsDropdownCard(
                title = "Accent Color",
                subtitle = "Customize app accent color",
                options = listOf("violet" to "Violet", "pink" to "Pink", "blue" to "Blue", "green" to "Green"),
                selectedOption = accentColor,
                onSelected = { viewModel.setAccentColor(it) }
            )

            SettingsSwitchRow(
                title = "Haptic Feedback",
                subtitle = "Vibrate on button press",
                checked = hapticEnabled,
                onCheckedChange = { viewModel.setHapticEnabled(it) }
            )

            SettingsSwitchRow(
                title = "Button Animations",
                subtitle = "Smooth tactile press animations",
                checked = animationEnabled,
                onCheckedChange = { viewModel.setAnimationEnabled(it) }
            )

            SettingsSwitchRow(
                title = "Large Display Text",
                subtitle = "Increase calculator font size",
                checked = largeText,
                onCheckedChange = { viewModel.setLargeText(it) }
            )

            // Calculator Preferences
            SettingsSectionHeader("Calculator Preferences")

            SettingsDropdownCard(
                title = "Decimal Precision",
                subtitle = "Limit fraction digits in results",
                options = listOf("auto" to "Automatic", "2" to "2 Decimal Places", "4" to "4 Decimal Places", "6" to "6 Decimal Places", "8" to "8 Decimal Places"),
                selectedOption = precision,
                onSelected = { viewModel.setDecimalPrecision(it) }
            )

            SettingsSwitchRow(
                title = "Thousands Separator",
                subtitle = "Format large numbers with commas",
                checked = thousands,
                onCheckedChange = { viewModel.setThousandsSeparator(it) }
            )

            SettingsSwitchRow(
                title = "Trailing Zeros",
                subtitle = "Always show fixed fraction digits",
                checked = trailing,
                onCheckedChange = { viewModel.setTrailingZeros(it) }
            )

            SettingsDropdownCard(
                title = "Trigonometry Angle Mode",
                subtitle = "Unit for sin, cos, and tan functions",
                options = listOf("deg" to "Degrees (deg)", "rad" to "Radians (rad)"),
                selectedOption = angleMode,
                onSelected = { viewModel.setAngleMode(it) }
            )

            SettingsSwitchRow(
                title = "Sound Feedback",
                subtitle = "Play audio click on key press",
                checked = soundEnabled,
                onCheckedChange = { viewModel.setSoundEnabled(it) }
            )

            // History & Storage
            SettingsSectionHeader("History & Storage")

            SettingsSwitchRow(
                title = "Enable Calculation History",
                subtitle = "Save past calculations locally",
                checked = historyEnabled,
                onCheckedChange = { viewModel.setHistoryEnabled(it) }
            )

            OutlinedButton(
                onClick = { showClearHistoryDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Clear Calculation History", color = MaterialTheme.colorScheme.error)
            }

            OutlinedButton(
                onClick = { showResetDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Reset All Settings")
            }
        }
    }

    if (showClearHistoryDialog) {
        AlertDialog(
            onDismissRequest = { showClearHistoryDialog = false },
            title = { Text("Clear History") },
            text = { Text("Are you sure you want to delete all calculation history?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.clearHistory()
                    showClearHistoryDialog = false
                }) {
                    Text("Clear", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearHistoryDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset Settings") },
            text = { Text("Are you sure you want to restore all settings to default?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.resetSettings()
                    showResetDialog = false
                }) {
                    Text("Reset", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
    )
}

@Composable
fun SettingsSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsDropdownCard(
    title: String,
    subtitle: String,
    options: List<Pair<String, String>>,
    selectedOption: String,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedLabel = options.find { it.first == selectedOption }?.second ?: selectedOption

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                TextButton(onClick = { expanded = !expanded }) {
                    Text(selectedLabel, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }
            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Divider(color = MaterialTheme.colorScheme.outline)
                Spacer(modifier = Modifier.height(8.dp))
                options.forEach { (key, label) ->
                    DropdownMenuItem(
                        text = { Text(label) },
                        onClick = {
                            onSelected(key)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
