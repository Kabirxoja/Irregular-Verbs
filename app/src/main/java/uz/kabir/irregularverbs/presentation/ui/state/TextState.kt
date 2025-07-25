package uz.kabir.irregularverbs.presentation.ui.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object TextState {
    var currentTextSize by mutableStateOf(TextMode.BIG)
}