package br.com.inovagabv2.core.designsystem.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.inovagabv2.core.designsystem.AguiaColors
import br.com.inovagabv2.domain.model.Project
import br.com.inovagabv2.domain.model.ProjectStatus

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
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AguiaColors.CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = project.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AguiaColors.NavyDark,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Status Chip (e.g. "Em andamento" or "Planejamento")
            val isPlanning = project.status == ProjectStatus.PLANEJADO
            val statusText = if (isPlanning) "Planejamento" else "Em andamento"
            val statusBg = if (isPlanning) AguiaColors.PrimaryBlue.copy(alpha = 0.12f) else AguiaColors.SuccessGreen.copy(alpha = 0.12f)
            val statusColor = if (isPlanning) AguiaColors.PrimaryBlue else AguiaColors.SuccessGreen

            Surface(
                color = statusBg,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.labelSmall,
                    color = statusColor,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress Bar Row: [ ===== Progress Bar ===== ] 65% >
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                LinearProgressIndicator(
                    progress = { project.progress },
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp),
                    color = AguiaColors.PrimaryBlue,
                    trackColor = AguiaColors.PrimaryBlue.copy(alpha = 0.15f),
                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Text(
                    text = "${(project.progress * 100).toInt()}%",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = AguiaColors.TextSecondary
                )

                Spacer(modifier = Modifier.width(8.dp))

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = AguiaColors.TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Details Row: Prazo | Investimento
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "Prazo",
                        tint = AguiaColors.TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = "Prazo",
                            fontSize = 11.sp,
                            color = AguiaColors.TextSecondary
                        )
                        Text(
                            text = project.deadline,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = AguiaColors.NavyDark
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AttachMoney,
                        contentDescription = "Investimento",
                        tint = AguiaColors.TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Column {
                        Text(
                            text = "Investimento",
                            fontSize = 11.sp,
                            color = AguiaColors.TextSecondary
                        )
                        Text(
                            text = project.investment,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = AguiaColors.NavyDark
                        )
                    }
                }
            }
        }
    }
}
