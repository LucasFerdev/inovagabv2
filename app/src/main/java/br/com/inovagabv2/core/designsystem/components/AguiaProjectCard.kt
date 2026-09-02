package br.com.inovagabv2.core.designsystem.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import br.com.inovagabv2.core.designsystem.AguiaColors
import br.com.inovagabv2.domain.model.Project

@Composable
fun AguiaProjectCard(
    project: Project,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = AguiaColors.CardWhite
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Projeto #${project.id.takeLast(4)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = AguiaColors.PrimaryBlue
                )
                // We might need a ProjectStatus chip too, re-using AguiaStatusChip if possible or create a specific one
                // For now, let's use a simple chip style if project has status
                Surface(
                    color = AguiaColors.Background,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = project.status.name,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        color = AguiaColors.TextSecondary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = project.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = AguiaColors.TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            AguiaProgressBar(progress = project.progress)
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Prazo",
                        style = MaterialTheme.typography.labelSmall,
                        color = AguiaColors.TextSecondary
                    )
                    Text(
                        text = project.deadline,
                        style = MaterialTheme.typography.bodySmall,
                        color = AguiaColors.TextPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Investimento",
                        style = MaterialTheme.typography.labelSmall,
                        color = AguiaColors.TextSecondary
                    )
                    Text(
                        text = project.investment,
                        style = MaterialTheme.typography.bodySmall,
                        color = AguiaColors.TextPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
