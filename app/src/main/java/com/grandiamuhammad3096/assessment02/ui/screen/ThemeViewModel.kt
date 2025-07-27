package com.grandiamuhammad3096.assessment02.ui.screen

import android.content.Context
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ThemeViewModel(context: Context): ViewModel() {
    private val prefs = context.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
    private val _isDark = MutableStateFlow(loadThemePref())
    val isDark: StateFlow<Boolean> get() = _isDark

    fun toggleTheme() {
        val newTheme = !_isDark.value
        _isDark.value = newTheme
        prefs.edit().putBoolean("is_dark_mode", newTheme).apply()
    }
    private fun loadThemePref(): Boolean = prefs.getBoolean("is_dark_mode", false)
}