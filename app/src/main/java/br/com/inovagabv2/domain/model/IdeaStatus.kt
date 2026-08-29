package br.com.inovagabv2.domain.model

import br.com.inovagabv2.core.designsystem.AguiaColors
import androidx.compose.ui.graphics.Color

enum class IdeaStatus(val displayName: String) {
    ENVIADA("Enviada"),
    EM_ANALISE("Em análise"),
    AJUSTES_SOLICITADOS("Ajustes solicitados"),
    APROVADA("Aprovada"),
    REJEITADA("Rejeitada"),
    EM_IMPLEMENTACAO("Em implementação"),
    CONCLUIDA("Concluída");

    fun getColor(): Color = when (this) {
        ENVIADA -> AguiaColors.StatusSent
        EM_ANALISE -> AguiaColors.StatusInAnalysis
        AJUSTES_SOLICITADOS -> AguiaColors.StatusAdjustRequested
        APROVADA -> AguiaColors.StatusApproved
        REJEITADA -> AguiaColors.StatusRejected
        EM_IMPLEMENTACAO -> AguiaColors.StatusImplementing
        CONCLUIDA -> AguiaColors.StatusCompleted
    }
}
