package br.com.inovagabv2.core.designsystem

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = AguiaColors.PrimaryBlue,
    secondary = AguiaColors.SecondaryBlue,
    tertiary = AguiaColors.SuccessGreen,
    background = AguiaColors.Background,
    surface = AguiaColors.CardWhite,
    onPrimary = AguiaColors.CardWhite,
    onSecondary = AguiaColors.CardWhite,
    onTertiary = AguiaColors.CardWhite,
    onBackground = AguiaColors.TextPrimary,
    onSurface = AguiaColors.TextPrimary,
    error = AguiaColors.ErrorRed
)

@Composable
fun AguiaTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = AguiaTypography,
        content = content
    )
}
