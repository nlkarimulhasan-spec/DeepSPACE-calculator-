package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.CalculationDatabase
import com.example.data.CalculationEntity
import com.example.data.SettingsManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.*

class CalculatorViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = CalculationDatabase.getDatabase(application).calculationDao()
    val settingsManager = SettingsManager(application)

    val history: Flow<List<CalculationEntity>> = dao.getAllCalculations()

    val themeMode: StateFlow<String> = settingsManager.themeMode.stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000), "light")
    val accentColor: StateFlow<String> = settingsManager.accentColor.stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000), "violet")
    val hapticEnabled: StateFlow<Boolean> = settingsManager.hapticEnabled.stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000), true)
    val animationEnabled: StateFlow<Boolean> = settingsManager.animationEnabled.stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000), true)
    val largeTextEnabled: StateFlow<Boolean> = settingsManager.largeTextEnabled.stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000), false)
    val decimalPrecision: StateFlow<String> = settingsManager.decimalPrecision.stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000), "auto")
    val thousandsSeparator: StateFlow<Boolean> = settingsManager.thousandsSeparator.stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000), false)
    val trailingZeros: StateFlow<Boolean> = settingsManager.trailingZeros.stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000), false)
    val angleMode: StateFlow<String> = settingsManager.angleMode.stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000), "deg")
    val soundEnabled: StateFlow<Boolean> = settingsManager.soundEnabled.stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000), false)
    val historyEnabled: StateFlow<Boolean> = settingsManager.historyEnabled.stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000), true)

    private val _expression = MutableStateFlow("")
    val expression: StateFlow<String> = _expression.asStateFlow()

    private val _result = MutableStateFlow("0")
    val result: StateFlow<String> = _result.asStateFlow()

    private var isEvaluated = false

    fun onButtonClick(value: String) {
        if (isEvaluated) {
            if (value in listOf("+", "-", "×", "÷", "%", "^")) {
                isEvaluated = false
            } else {
                _expression.value = ""
                isEvaluated = false
            }
        }

        when (value) {
            "AC" -> {
                _expression.value = ""
                _result.value = "0"
                isEvaluated = false
            }
            "⌫" -> {
                val current = _expression.value
                if (current.isNotEmpty()) {
                    _expression.value = current.dropLast(1)
                    updatePreview()
                }
            }
            "=" -> {
                calculateAndSave()
            }
            "±" -> {
                val current = _expression.value
                if (current.isNotEmpty()) {
                    if (current.startsWith("-")) {
                        _expression.value = current.removePrefix("-")
                    } else {
                        _expression.value = "-$current"
                    }
                    updatePreview()
                }
            }
            "√" -> {
                _expression.value += "√("
                updatePreview()
            }
            "x²" -> {
                _expression.value += "^2"
                updatePreview()
            }
            "1/x" -> {
                _expression.value = "1/(${_expression.value})"
                updatePreview()
            }
            "sin", "cos", "tan", "log", "ln" -> {
                _expression.value += "$value("
                updatePreview()
            }
            else -> {
                _expression.value += value
                updatePreview()
            }
        }
    }

    private fun updatePreview() {
        val expr = _expression.value
        if (expr.isEmpty()) {
            _result.value = "0"
            return
        }
        try {
            val evalResult = evaluateExpression(expr, angleMode.value)
            _result.value = formatResult(evalResult)
        } catch (e: Exception) {
            // Keep previous preview
        }
    }

    private fun calculateAndSave() {
        val expr = _expression.value
        if (expr.isEmpty()) return
        try {
            val evalResult = evaluateExpression(expr, angleMode.value)
            val formattedResult = formatResult(evalResult)
            _result.value = formattedResult

            if (historyEnabled.value) {
                viewModelScope.launch {
                    dao.insert(CalculationEntity(expression = expr, result = formattedResult))
                }
            }

            _expression.value = formattedResult
            isEvaluated = true
        } catch (e: Exception) {
            _result.value = "Error"
        }
    }

    fun clearHistory() {
        viewModelScope.launch { dao.deleteAll() }
    }

    fun loadHistoryItem(item: CalculationEntity) {
        _expression.value = item.expression
        _result.value = item.result
        isEvaluated = true
    }

    // Settings mutators
    fun setThemeMode(mode: String) { viewModelScope.launch { settingsManager.setThemeMode(mode) } }
    fun setAccentColor(color: String) { viewModelScope.launch { settingsManager.setAccentColor(color) } }
    fun setHapticEnabled(enabled: Boolean) { viewModelScope.launch { settingsManager.setHapticEnabled(enabled) } }
    fun setAnimationEnabled(enabled: Boolean) { viewModelScope.launch { settingsManager.setAnimationEnabled(enabled) } }
    fun setLargeText(enabled: Boolean) { viewModelScope.launch { settingsManager.setLargeText(enabled) } }
    fun setDecimalPrecision(precision: String) { viewModelScope.launch { settingsManager.setDecimalPrecision(precision) } }
    fun setThousandsSeparator(enabled: Boolean) { viewModelScope.launch { settingsManager.setThousandsSeparator(enabled) } }
    fun setTrailingZeros(enabled: Boolean) { viewModelScope.launch { settingsManager.setTrailingZeros(enabled) } }
    fun setAngleMode(mode: String) { viewModelScope.launch { settingsManager.setAngleMode(mode) } }
    fun setSoundEnabled(enabled: Boolean) { viewModelScope.launch { settingsManager.setSoundEnabled(enabled) } }
    fun setHistoryEnabled(enabled: Boolean) { viewModelScope.launch { settingsManager.setHistoryEnabled(enabled) } }
    fun resetSettings() { viewModelScope.launch { settingsManager.resetSettings() } }

    private fun evaluateExpression(expr: String, angleModeStr: String): Double {
        val sanitized = expr.replace("×", "*").replace("÷", "/")
        val parser = ExpressionParser(sanitized, angleModeStr)
        return parser.parse()
    }

    private fun formatResult(value: Double): String {
        if (value.isNaN() || value.isInfinite()) return "Error"

        val precisionVal = decimalPrecision.value
        val useThousands = thousandsSeparator.value
        val showTrailing = trailingZeros.value

        val numFormat = DecimalFormat.getInstance(Locale.US) as DecimalFormat
        val symbols = DecimalFormatSymbols(Locale.US)
        numFormat.decimalFormatSymbols = symbols

        numFormat.isGroupingUsed = useThousands

        when (precisionVal) {
            "2" -> numFormat.maximumFractionDigits = 2
            "4" -> numFormat.maximumFractionDigits = 4
            "6" -> numFormat.maximumFractionDigits = 6
            "8" -> numFormat.maximumFractionDigits = 8
            else -> numFormat.maximumFractionDigits = 8
        }

        if (showTrailing && precisionVal != "auto") {
            numFormat.minimumFractionDigits = precisionVal.toInt()
        } else if (!showTrailing) {
            numFormat.minimumFractionDigits = 0
        }

        return numFormat.format(value)
    }

    private class ExpressionParser(private val input: String, private val angleMode: String) {
        private var pos = 0
        private var ch = if (input.isNotEmpty()) input[0] else '\u0000'

        private fun nextChar() {
            pos++
            ch = if (pos < input.length) input[pos] else '\u0000'
        }

        private fun eat(charToEat: Char): Boolean {
            while (ch == ' ') nextChar()
            if (ch == charToEat) {
                nextChar()
                return true
            }
            return false
        }

        fun parse(): Double {
            pos = -1
            nextChar()
            val x = parseExpression()
            if (pos < input.length) throw RuntimeException("Unexpected: $ch")
            return x
        }

        private fun parseExpression(): Double {
            var x = parseTerm()
            while (true) {
                when {
                    eat('+') -> x += parseTerm()
                    eat('-') -> x -= parseTerm()
                    else -> return x
                }
            }
        }

        private fun parseTerm(): Double {
            var x = parseFactor()
            while (true) {
                when {
                    eat('*') -> x *= parseFactor()
                    eat('/') -> {
                        val denom = parseFactor()
                        if (denom == 0.0) throw ArithmeticException("Division by zero")
                        x /= denom
                    }
                    else -> return x
                }
            }
        }

        private fun parseFactor(): Double {
            if (eat('+')) return parseFactor()
            if (eat('-')) return -parseFactor()

            var x: Double
            val startPos = pos

            if (eat('(')) {
                x = parseExpression()
                eat(')')
            } else if (ch.isLetter()) {
                val funcStart = pos
                while (ch.isLetter()) nextChar()
                val func = input.substring(funcStart, pos)
                eat('(')
                val arg = parseExpression()
                eat(')')
                x = when (func) {
                    "sqrt", "√" -> {
                        if (arg < 0.0) throw ArithmeticException("Invalid sqrt")
                        sqrt(arg)
                    }
                    "sin" -> {
                        val rad = if (angleMode == "deg") Math.toRadians(arg) else arg
                        sin(rad)
                    }
                    "cos" -> {
                        val rad = if (angleMode == "deg") Math.toRadians(arg) else arg
                        cos(rad)
                    }
                    "tan" -> {
                        val rad = if (angleMode == "deg") Math.toRadians(arg) else arg
                        tan(rad)
                    }
                    "log" -> {
                        if (arg <= 0.0) throw ArithmeticException("Invalid log")
                        log10(arg)
                    }
                    "ln" -> {
                        if (arg <= 0.0) throw ArithmeticException("Invalid ln")
                        ln(arg)
                    }
                    else -> arg
                }
            } else if (eat('√')) {
                val arg = parseFactor()
                if (arg < 0.0) throw ArithmeticException("Invalid sqrt")
                x = sqrt(arg)
            } else if ((ch in '0'..'9') || ch == '.') {
                while ((ch in '0'..'9') || ch == '.') nextChar()
                val numStr = input.substring(startPos, pos)
                x = numStr.toDoubleOrNull() ?: 0.0
            } else {
                throw RuntimeException("Unexpected: $ch")
            }

            // Power operator ^
            if (eat('^')) {
                val exp = parseFactor()
                x = x.pow(exp)
            }

            if (eat('%')) {
                x /= 100.0
            }
            return x
        }
    }
}
