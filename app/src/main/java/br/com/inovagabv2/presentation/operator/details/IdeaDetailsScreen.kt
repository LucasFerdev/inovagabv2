package br.com.inovagabv2.presentation.operator.details

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.inovagabv2.core.designsystem.AguiaColors
import br.com.inovagabv2.core.designsystem.components.AguiaTimeline
import br.com.inovagabv2.core.designsystem.components.AguiaTopBar

@Composable
fun IdeaDetailsScreen(
    viewModel: IdeaDetailsViewModel,
    onBackClick: () -> Unit
) {
    val idea by viewModel.idea.collectAsState()

    Scaffold(
        topBar = {
            AguiaTopBar(
                roleTag = "OPERADOR",
                onBackClick = onBackClick
            )
        },
        containerColor = AguiaColors.Background
    ) { padding ->
        idea?.let { item ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Header Area
                Column {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = AguiaColors.NavyDark
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Status Pill Tag
                    Surface(
                        color = AguiaColors.SuccessGreen.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = item.status.displayName,
                            style = MaterialTheme.typography.labelSmall,
                            color = AguiaColors.SuccessGreen,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Date & Folder Category Row
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Enviada em ${item.createdAt}",
                            fontSize = 12.sp,
                            color = AguiaColors.TextSecondary
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Icon(
                            imageVector = Icons.Default.Folder,
                            contentDescription = null,
                            tint = AguiaColors.TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = item.category,
                            fontSize = 12.sp,
                            color = AguiaColors.TextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                HorizontalDivider(color = AguiaColors.TextSecondary.copy(alpha = 0.15f))

                // Description
                Section(title = "Descrição") {
                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = AguiaColors.NavyDark,
                        lineHeight = 22.sp
                    )
                }

                // Benefits
                Section(title = "Benefícios esperados") {
                    Text(
                        text = item.benefits,
                        style = MaterialTheme.typography.bodyMedium,
                        color = AguiaColors.NavyDark,
                        lineHeight = 22.sp
                    )
                }

                // Timeline / Acompanhamento
                if (item.timeline.isNotEmpty()) {
                    Section(title = "Acompanhamento") {
                        AguiaTimeline(events = item.timeline)
                    }
                }
            }
        } ?: run {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AguiaColors.PrimaryBlue)
            }
        }
    }
}

@Composable
private fun Section(title: String, content: @Composable () -> Unit) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = AguiaColors.NavyDark
        )
        Spacer(modifier = Modifier.height(8.dp))
        content()
    }
}
