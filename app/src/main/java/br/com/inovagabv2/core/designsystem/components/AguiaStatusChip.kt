package br.com.inovagabv2.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.inovagabv2.domain.model.IdeaStatus

@Composable
fun AguiaStatusChip(
    statusText: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color? = null,
    textColor: Color? = null
) {
    val (bg, txt) = if (backgroundColor != null && textColor != null) {
        backgroundColor to textColor
    } else {
        getStatusColors(statusText)
    }

    Box(
        modifier = modifier
            .background(
                color = bg,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = statusText.uppercase(),
            color = txt,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun AguiaStatusChip(
    status: IdeaStatus,
    modifier: Modifier = Modifier
) {
    val label = when (status) {
        IdeaStatus.EM_ANALISE -> "EM ANÁLISE"
        IdeaStatus.APROVADA -> "APROVADA"
        IdeaStatus.ENVIADA -> "ENVIADA"
        IdeaStatus.REJEITADA -> "REJEITADA"
        IdeaStatus.AJUSTES_SOLICITADOS -> "AJUSTES"
        IdeaStatus.EM_IMPLEMENTACAO -> "IMPLEMENTANDO"
        IdeaStatus.CONCLUIDA -> "CONCLUÍDA"
    }
    AguiaStatusChip(
        statusText = label,
        modifier = modifier
    )
}

private fun getStatusColors(statusText: String): Pair<Color, Color> {
    val s = statusText.uppercase().trim()
    return when {
        s.contains("ANALISE") || s.contains("ANÁLISE") -> Color(0xFFFEF3C7) to Color(0xFFD97706)
        s.contains("APROVADA") || s.contains("ATIVA") || s.contains("CONCLUÍDA") -> Color(0xFFD1FAE5) to Color(0xFF059669)
        s.contains("ENVIADA") -> Color(0xFFE0F2FE) to Color(0xFF0284C7)
        s.contains("REJEITADA") -> Color(0xFFFEE2E2) to Color(0xFFDC2626)
        else -> Color(0xFFF1F5F9) to Color(0xFF475569)
    }
}
