package uz.kabir.irregularverbs.presentation.ui.screens.option

data class OptionItem(
    val baseForm: String,
    val visiblePart: String,
    val isVerb2Hidden: Boolean,
    val options: List<String>,
    val correctAnswer: String,
    val translation: String,
    val selectedAnswer: String? = null
)