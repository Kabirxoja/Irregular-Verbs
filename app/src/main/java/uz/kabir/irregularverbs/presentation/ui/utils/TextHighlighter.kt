package uz.kabir.irregularverbs.presentation.ui.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import kotlin.text.substring

fun String.toHighlightedColorText(color: Color): AnnotatedString {
    return buildAnnotatedString {
        var currentIndex = 0
        val regex = "#(.*?)#".toRegex()
        for (match in regex.findAll(this@toHighlightedColorText)) {
            val range = match.range
            val word = match.groupValues[1]
            append(this@toHighlightedColorText.substring(currentIndex, range.first))
            withStyle(style = SpanStyle(color = color)) { append(word) }
            currentIndex = range.last + 1
        }
        if (currentIndex < this@toHighlightedColorText.length) {
            append(this@toHighlightedColorText.substring(currentIndex))
        }
    }
}
