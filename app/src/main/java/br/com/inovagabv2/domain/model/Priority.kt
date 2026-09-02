package br.com.inovagabv2.domain.model

import br.com.inovagabv2.core.designsystem.AguiaColors
import androidx.compose.ui.graphics.Color

enum class Priority(val displayName: String) {
    BAIXA("Baixa"),
    MEDIA("Média"),
    ALTA("Alta");

    fun getColor(): Color = when (this) {
        BAIXA -> AguiaColors.PriorityLow
        MEDIA -> AguiaColors.PriorityMedium
        ALTA -> AguiaColors.PriorityHigh
    }
}
