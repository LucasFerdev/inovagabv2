package br.com.inovagabv2.domain.model

import br.com.inovagabv2.core.designsystem.AguiaColors
import androidx.compose.ui.graphics.Color

enum class IdeaStatus(val displayName: String) {
    ENVIADA("Enviada"),
    EM_ANALISE("Em análise"),
    APROVADA("Aprovada"),
    REJEITADA("Rejeitada"),
    ARQUIVADA("Arquivada");

    fun getColor(): Color = when (this) {
        ENVIADA -> AguiaColors.StatusSent
        EM_ANALISE -> AguiaColors.StatusInAnalysis
        APROVADA -> AguiaColors.StatusApproved
        REJEITADA -> AguiaColors.StatusRejected
        ARQUIVADA -> AguiaColors.TextSecondary
    }
}
