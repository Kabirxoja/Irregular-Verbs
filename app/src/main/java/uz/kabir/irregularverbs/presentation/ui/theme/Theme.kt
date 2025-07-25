package uz.kabir.irregularverbs.presentation.ui.theme

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import uz.kabir.irregularverbs.R
import uz.kabir.irregularverbs.presentation.ui.state.TextMode
import uz.kabir.irregularverbs.presentation.ui.state.TextState
import uz.kabir.irregularverbs.presentation.ui.state.ThemeMode
import uz.kabir.irregularverbs.presentation.ui.state.ThemeState


@Composable
fun IrregularVerbsTheme(
    themeMode: ThemeMode,
    content: @Composable () -> Unit
) {
    val isDarkTheme = when (themeMode) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val textSize = TextState.currentTextSize

    val colors = if (isDarkTheme) {
        baseDarkPalette
    } else {
        baseLightPalette
    }

    val myFontFamily = FontFamily(Font(R.font.mplusrounded1c_extrabold))

    val typography = CustomThemeTypography(
        veryLargeText = TextStyle(
            fontSize = when (textSize) {
                TextMode.SMALL -> 28.sp
                TextMode.MEDIUM -> 32.sp
                TextMode.BIG -> 34.sp
            },
            fontWeight = FontWeight.Bold,
            fontFamily = myFontFamily

        ),
        largeText = TextStyle(
            fontSize = when (textSize) {
                TextMode.SMALL -> 22.sp
                TextMode.MEDIUM -> 24.sp
                TextMode.BIG -> 26.sp
            },
            fontWeight = FontWeight.SemiBold,
            fontFamily = myFontFamily

        ),
        mediumText = TextStyle(
            fontSize = when (textSize) {
                TextMode.SMALL -> 16.sp
                TextMode.MEDIUM -> 18.sp
                TextMode.BIG -> 20.sp
            },
            fontWeight = FontWeight.Medium,
            fontFamily = myFontFamily

        ),
        smallText = TextStyle(
            fontSize = when (textSize) {
                TextMode.SMALL -> 12.sp
                TextMode.MEDIUM -> 14.sp
                TextMode.BIG -> 16.sp
            },
            fontWeight = FontWeight.Normal,
            fontFamily = myFontFamily

        ),
        verySmallText = TextStyle(
            fontSize = when (textSize) {
                TextMode.SMALL -> 8.sp
                TextMode.MEDIUM -> 10.sp
                TextMode.BIG -> 12.sp
            },
            fontWeight = FontWeight.Light,
            fontFamily = myFontFamily
        )
    )


    CompositionLocalProvider(
        LocalCustomThemeColors provides colors,
        LocalCustomThemeTypography provides typography,
        content = content
    )
}
