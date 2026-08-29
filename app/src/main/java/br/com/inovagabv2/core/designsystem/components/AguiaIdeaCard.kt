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
import br.com.inovagabv2.domain.model.Idea

@Composable
fun AguiaIdeaCard(
    idea: Idea,
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
                    text = idea.category.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = AguiaColors.PrimaryBlue
                )
                AguiaStatusChip(status = idea.status)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = idea.title,
                style = MaterialTheme.typography.titleLarge,
                color = AguiaColors.TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = idea.description,
                style = MaterialTheme.typography.bodyMedium,
                color = AguiaColors.TextSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Enviada em ${idea.createdAt}",
                    style = MaterialTheme.typography.labelSmall,
                    color = AguiaColors.TextSecondary
                )
            }
        }
    }
}
