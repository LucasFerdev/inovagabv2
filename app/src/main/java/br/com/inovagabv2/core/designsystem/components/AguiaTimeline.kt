package br.com.inovagabv2.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.com.inovagabv2.core.designsystem.AguiaColors
import br.com.inovagabv2.domain.model.TimelineEvent

@Composable
fun AguiaTimeline(
    events: List<TimelineEvent>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        events.forEachIndexed { index, event ->
            TimelineItem(
                event = event,
                isLast = index == events.size - 1
            )
        }
    }
}

@Composable
private fun TimelineItem(
    event: TimelineEvent,
    isLast: Boolean
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(
                        if (event.isCompleted) AguiaColors.PrimaryBlue 
                        else AguiaColors.TextSecondary.copy(alpha = 0.5f)
                    )
            )
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(40.dp)
                        .background(AguiaColors.TextSecondary.copy(alpha = 0.2f))
                )
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.padding(bottom = 24.dp)) {
            Text(
                text = event.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = if (event.isCompleted) AguiaColors.TextPrimary else AguiaColors.TextSecondary
            )
            Text(
                text = event.date,
                style = MaterialTheme.typography.labelSmall,
                color = AguiaColors.TextSecondary
            )
            if (event.description != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = event.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = AguiaColors.TextSecondary
                )
            }
        }
    }
}
