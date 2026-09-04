package br.com.inovagabv2.core.designsystem.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.com.inovagabv2.core.designsystem.AguiaColors

@Composable
fun SparklineGraph(
    modifier: Modifier = Modifier,
    lineColor: Color = AguiaColors.SuccessGreen,
    isPositive: Boolean = true
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        
        val path = Path().apply {
            if (isPositive) {
                moveTo(0f, h * 0.8f)
                cubicTo(w * 0.3f, h * 0.9f, w * 0.6f, h * 0.35f, w * 0.85f, h * 0.25f)
                lineTo(w, h * 0.05f)
            } else {
                moveTo(0f, h * 0.2f)
                cubicTo(w * 0.3f, h * 0.1f, w * 0.6f, h * 0.65f, w * 0.85f, h * 0.75f)
                lineTo(w, h * 0.95f)
            }
        }
        
        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}

@Composable
fun AguiaDashboardCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    color: Color = AguiaColors.PrimaryBlue,
    showSparkline: Boolean = true,
    isPositiveSparkline: Boolean = true,
    trend: String? = null
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AguiaColors.CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    color = AguiaColors.TextSecondary
                )
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = AguiaColors.NavyDark
            )
            
            if (showSparkline) {
                Spacer(modifier = Modifier.height(12.dp))
                SparklineGraph(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(24.dp),
                    isPositive = isPositiveSparkline
                )
            } else if (trend != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = trend,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (trend.startsWith("+")) AguiaColors.SuccessGreen else AguiaColors.ErrorRed
                )
            }
        }
    }
}
