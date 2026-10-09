package com.example.data

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsManager(private val context: Context) {
    companion object {
        val THEME_MODE = stringPreferencesKey("theme_mode") // "light", "dark", "system"
        val ACCENT_COLOR = stringPreferencesKey("accent_color") // "violet", "pink", "blue", "green"
        val HAPTIC_ENABLED = booleanPreferencesKey("haptic_enabled")
        val ANIMATION_ENABLED = booleanPreferencesKey("animation_enabled")
        val LARGE_TEXT = booleanPreferencesKey("large_text")
        val DECIMAL_PRECISION = stringPreferencesKey("decimal_precision") // "auto", "2", "4", "6", "8"
        val THOUSANDS_SEPARATOR = booleanPreferencesKey("thousands_separator")
        val TRAILING_ZEROS = booleanPreferencesKey("trailing_zeros")
        val ANGLE_MODE = stringPreferencesKey("angle_mode") // "deg", "rad"
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val HISTORY_ENABLED = booleanPreferencesKey("history_enabled")
    }

    val themeMode: Flow<String> = context.dataStore.data.map { it[THEME_MODE] ?: "light" }
    val accentColor: Flow<String> = context.dataStore.data.map { it[ACCENT_COLOR] ?: "violet" }
    val hapticEnabled: Flow<Boolean> = context.dataStore.data.map { it[HAPTIC_ENABLED] ?: true }
    val animationEnabled: Flow<Boolean> = context.dataStore.data.map { it[ANIMATION_ENABLED] ?: true }
    val largeTextEnabled: Flow<Boolean> = context.dataStore.data.map { it[LARGE_TEXT] ?: false }
    val decimalPrecision: Flow<String> = context.dataStore.data.map { it[DECIMAL_PRECISION] ?: "auto" }
    val thousandsSeparator: Flow<Boolean> = context.dataStore.data.map { it[THOUSANDS_SEPARATOR] ?: false }
    val trailingZeros: Flow<Boolean> = context.dataStore.data.map { it[TRAILING_ZEROS] ?: false }
    val angleMode: Flow<String> = context.dataStore.data.map { it[ANGLE_MODE] ?: "deg" }
    val soundEnabled: Flow<Boolean> = context.dataStore.data.map { it[SOUND_ENABLED] ?: false }
    val historyEnabled: Flow<Boolean> = context.dataStore.data.map { it[HISTORY_ENABLED] ?: true }

    suspend fun setThemeMode(mode: String) { context.dataStore.edit { it[THEME_MODE] = mode } }
    suspend fun setAccentColor(color: String) { context.dataStore.edit { it[ACCENT_COLOR] = color } }
    suspend fun setHapticEnabled(enabled: Boolean) { context.dataStore.edit { it[HAPTIC_ENABLED] = enabled } }
    suspend fun setAnimationEnabled(enabled: Boolean) { context.dataStore.edit { it[ANIMATION_ENABLED] = enabled } }
    suspend fun setLargeText(enabled: Boolean) { context.dataStore.edit { it[LARGE_TEXT] = enabled } }
    suspend fun setDecimalPrecision(precision: String) { context.dataStore.edit { it[DECIMAL_PRECISION] = precision } }
    suspend fun setThousandsSeparator(enabled: Boolean) { context.dataStore.edit { it[THOUSANDS_SEPARATOR] = enabled } }
    suspend fun setTrailingZeros(enabled: Boolean) { context.dataStore.edit { it[TRAILING_ZEROS] = enabled } }
    suspend fun setAngleMode(mode: String) { context.dataStore.edit { it[ANGLE_MODE] = mode } }
    suspend fun setSoundEnabled(enabled: Boolean) { context.dataStore.edit { it[SOUND_ENABLED] = enabled } }
    suspend fun setHistoryEnabled(enabled: Boolean) { context.dataStore.edit { it[HISTORY_ENABLED] = enabled } }

    suspend fun resetSettings() {
        context.dataStore.edit { it.clear() }
    }
}
