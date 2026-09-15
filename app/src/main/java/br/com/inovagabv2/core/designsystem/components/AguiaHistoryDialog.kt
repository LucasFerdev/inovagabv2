package br.com.inovagabv2.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.inovagabv2.core.designsystem.AguiaColors
import br.com.inovagabv2.domain.model.HistoryItem

@Composable
fun AguiaHistoryDialog(
    historyItems: List<HistoryItem>,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    title: String = "Histórico de Alterações"
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = AguiaColors.NavyDark
            )
        },
        text = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 350.dp)
            ) {
                if (isLoading) {
                    Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = AguiaColors.PrimaryBlue, modifier = Modifier.size(28.dp))
                    }
                } else if (historyItems.isEmpty()) {
                    Text(
                        text = "Nenhum histórico registrado para este item.",
                        fontSize = 13.sp,
                        color = AguiaColors.TextSecondary,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(historyItems) { item ->
                            HistoryRow(item = item)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Fechar", fontWeight = FontWeight.Bold, color = AguiaColors.PrimaryBlue)
            }
        }
    )
}

@Composable
private fun HistoryRow(item: HistoryItem) {
    Surface(
        color = Color(0xFFF8FAFC),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .offset(y = 4.dp)
                    .clip(CircleShape)
                    .background(AguiaColors.PrimaryBlue)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.action,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = AguiaColors.NavyDark
                )

                if (!item.dateTime.isBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = formatDatePtBr(item.dateTime),
                        fontSize = 11.sp,
                        color = AguiaColors.TextSecondary
                    )
                }

                item.status?.let { statusVal ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Status: $statusVal",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = AguiaColors.PrimaryBlue
                    )
                }

                item.justification?.let { just ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Justificativa: $just",
                        fontSize = 12.sp,
                        color = AguiaColors.TextSecondary
                    )
                }
            }
        }
    }
}

private fun formatDatePtBr(isoDate: String): String {
    return try {
        val parts = isoDate.split("T")
        if (parts.size >= 2) {
            val dateParts = parts[0].split("-")
            val timeParts = parts[1].take(5)
            if (dateParts.size >= 3) {
                "${dateParts[2]}/${dateParts[1]}/${dateParts[0]} às $timeParts"
            } else isoDate
        } else isoDate
    } catch (_: Exception) {
        isoDate
    }
}
