package uz.kabir.irregularverbs.presentation.ui.screens.listen

data class ListenItem(
    val verb1: String,
    val verb2: String,
    val verb3: String,
    val isVerb2Hidden: Boolean,
    val selected: Pair<String, String>,
    val isCorrect: Boolean,
    val example: String,
    val translationExample: String
)