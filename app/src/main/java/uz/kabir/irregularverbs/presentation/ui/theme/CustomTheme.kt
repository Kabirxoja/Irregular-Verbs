package uz.kabir.irregularverbs.presentation.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

data class CustomThemeColors(
    val mainGreen: Color,
    val mainBlue: Color,
    val mainRed: Color,
    val mainOrange: Color,
    val mainGray:Color,
    val mainYellow: Color,
    val textWhite: Color,
    val textBlackAndWhite: Color,
    val whiteToGray:Color,
    val backgroundColor: Color,
    val whiteToBlack:Color
)

data class CustomThemeTypography(
    val veryLargeText: TextStyle,
    val largeText: TextStyle,
    val mediumText: TextStyle,
    val smallText: TextStyle,
    val verySmallText: TextStyle
)

object CustomTheme {
    internal val colors: CustomThemeColors
        @Composable
        get() = LocalCustomThemeColors.current

    internal val typography: CustomThemeTypography
        @Composable
        get() = LocalCustomThemeTypography.current

}

internal val LocalCustomThemeColors = staticCompositionLocalOf<CustomThemeColors> {
    error("No colors provided")
}

internal val LocalCustomThemeTypography = staticCompositionLocalOf<CustomThemeTypography> {
    error("No typography provided")
}
