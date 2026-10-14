package com.dev.touyou.ffmultiplier.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.dev.touyou.ffmultiplier.R

/** iOS 版の FFColor と 1:1。ライト / ダークは values / values-night で切り替わる。 */
object FFColor {
    val blackBackground @Composable get() = colorResource(R.color.ff_black_background)
    val blackReversible @Composable get() = colorResource(R.color.ff_black_reversible)
    val brown @Composable get() = colorResource(R.color.ff_brown)
    val darkGray @Composable get() = colorResource(R.color.ff_dark_gray)
    val gray @Composable get() = colorResource(R.color.ff_gray)
    val green @Composable get() = colorResource(R.color.ff_green)
    val greenBackground @Composable get() = colorResource(R.color.ff_green_background)
    val lightGrayBackground @Composable get() = colorResource(R.color.ff_light_gray_background)
    val red @Composable get() = colorResource(R.color.ff_red)
    val settingGray @Composable get() = colorResource(R.color.ff_setting_gray)
    val white @Composable get() = colorResource(R.color.ff_white)
    val whiteBackground @Composable get() = colorResource(R.color.ff_white_background)
}

object FFFont {
    val dseg7 = FontFamily(Font(R.font.dseg7_classic_mini_bold))
    val futuraBold = FontFamily(Font(R.font.futura_bold))
    val futuraMedium = FontFamily(Font(R.font.futura_medium))
}

@Composable
fun FFTheme(content: @Composable () -> Unit) {
    val green = FFColor.green
    val red = FFColor.red
    val scheme = if (isSystemInDarkTheme()) {
        darkColorScheme(primary = green, onPrimary = Color.Black, error = red)
    } else {
        lightColorScheme(primary = green, onPrimary = Color.White, error = red)
    }
    MaterialTheme(colorScheme = scheme, content = content)
}
