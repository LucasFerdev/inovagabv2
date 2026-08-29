package br.com.inovagabv2.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import br.com.inovagabv2.core.designsystem.AguiaColors
import br.com.inovagabv2.domain.model.Priority

@Composable
fun AguiaPrioritySelector(
    selectedPriority: Priority?,
    onPrioritySelected: (Priority) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Prioridade",
            style = MaterialTheme.typography.titleSmall,
            color = AguiaColors.TextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Priority.entries.forEach { priority ->
                val isSelected = selectedPriority == priority
                val color = priority.getColor()
                
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isSelected) color.copy(alpha = 0.1f)
                            else Color.Transparent
                        )
                        .border(
                            width = 1.dp,
                            color = if (isSelected) color else AguiaColors.TextSecondary.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { onPrioritySelected(priority) }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = priority.displayName,
                        style = MaterialTheme.typography.labelLarge,
                        color = if (isSelected) color else AguiaColors.TextSecondary,
                        fontWeight = if (isSelected) androidx.compose.ui.text.font.FontWeight.Bold else null
                    )
                }
            }
        }
    }
}
