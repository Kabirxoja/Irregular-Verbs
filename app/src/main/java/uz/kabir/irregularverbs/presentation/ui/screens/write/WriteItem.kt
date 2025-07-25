package uz.kabir.irregularverbs.presentation.ui.screens.write

data class WriteItem(
    val verb1: String,
    val verb2: String,
    val verb3: String,
    val isVerb2Hidden: Boolean,
    val translation: String,
    val userAnswer: String = "",
    val isCorrect: Boolean = false,
    val example: String,
    val translationExample: String
)