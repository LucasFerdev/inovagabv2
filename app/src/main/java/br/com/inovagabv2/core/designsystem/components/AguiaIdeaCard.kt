package br.com.inovagabv2.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import br.com.inovagabv2.core.designsystem.AguiaColors
import br.com.inovagabv2.domain.model.Idea
import br.com.inovagabv2.domain.model.IdeaStatus
import br.com.inovagabv2.domain.model.Priority

@Composable
fun AguiaIdeaCard(
    idea: Idea,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val categoryIcon = when {
        idea.title.contains("embarque", ignoreCase = true) || idea.category.contains("oper", ignoreCase = true) -> Icons.Default.DirectionsBus
        idea.title.contains("manutenção", ignoreCase = true) || idea.title.contains("checklist", ignoreCase = true) -> Icons.Default.Build
        idea.title.contains("coleta", ignoreCase = true) || idea.category.contains("sustent", ignoreCase = true) -> Icons.Default.Eco
        idea.title.contains("wi-fi", ignoreCase = true) || idea.title.contains("wifi", ignoreCase = true) -> Icons.Default.Wifi
        else -> Icons.Default.Lightbulb
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AguiaColors.CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(AguiaColors.PrimaryBlue),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = categoryIcon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = idea.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AguiaColors.NavyDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = idea.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = AguiaColors.TextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = idea.authorName,
                    style = MaterialTheme.typography.labelSmall,
                    color = AguiaColors.TextSecondary,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val isApproved = idea.status == IdeaStatus.APROVADA
                    val isSent = idea.status == IdeaStatus.ENVIADA
                    val chipBg = when {
                        isApproved -> AguiaColors.SuccessGreen.copy(alpha = 0.12f)
                        isSent -> AguiaColors.PrimaryBlue.copy(alpha = 0.12f)
                        else -> AguiaColors.PrimaryBlue.copy(alpha = 0.12f)
                    }
                    val chipColor = when {
                        isApproved -> AguiaColors.SuccessGreen
                        isSent -> AguiaColors.PrimaryBlue
                        else -> AguiaColors.PrimaryBlue
                    }

                    Surface(
                        color = chipBg,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = idea.status.displayName,
                            style = MaterialTheme.typography.labelSmall,
                            color = chipColor,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    idea.priority?.let { priority ->
                        val priorityColor = when (priority) {
                            Priority.ALTA -> AguiaColors.PrimaryBlue
                            Priority.MEDIA -> if (isApproved) Color(0xFFEAB308) else Color(0xFF0284C7)
                            Priority.BAIXA -> AguiaColors.PrimaryBlue
                        }
                        Text(
                            text = priority.displayName,
                            style = MaterialTheme.typography.labelSmall,
                            color = priorityColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
