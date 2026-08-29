package br.com.inovagabv2.domain.model

import br.com.inovagabv2.core.designsystem.AguiaColors
import androidx.compose.ui.graphics.Color

enum class ProjectStatus(val displayName: String) {
    PLANEJADO("Planejado"),
    EM_ANDAMENTO("Em andamento"),
    CONCLUIDO("Concluído"),
    ATRASADO("Atrasado");

    fun getColor(): Color = when (this) {
        PLANEJADO -> AguiaColors.StatusSent
        EM_ANDAMENTO -> AguiaColors.StatusImplementing
        CONCLUIDO -> AguiaColors.StatusCompleted
        ATRASADO -> AguiaColors.ErrorRed
    }
}
