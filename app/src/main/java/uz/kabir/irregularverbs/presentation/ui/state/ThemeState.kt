package uz.kabir.irregularverbs.presentation.ui.state

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object ThemeState {
    var currentTheme by mutableStateOf(ThemeMode.SYSTEM)
    @Composable
    fun isDarkTheme(): Boolean {
        return when (currentTheme) {
            ThemeMode.DARK -> true
            ThemeMode.LIGHT -> false
            ThemeMode.SYSTEM -> isSystemInDarkTheme()
        }
    }
}