package br.com.inovagabv2.presentation.operator.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.com.inovagabv2.core.designsystem.AguiaColors
import br.com.inovagabv2.core.designsystem.components.AguiaStatusChip
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
                title = "Detalhes da Sugestão",
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
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Header Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = AguiaColors.CardWhite),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = item.category.uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                                color = AguiaColors.PrimaryBlue
                            )
                            AguiaStatusChip(status = item.status)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = AguiaColors.TextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Enviada em ${item.createdAt}",
                            style = MaterialTheme.typography.labelSmall,
                            color = AguiaColors.TextSecondary
                        )
                    }
                }

                // Description
                Section(title = "Descrição") {
                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = AguiaColors.TextPrimary
                    )
                }

                // Benefits
                Section(title = "Benefícios esperados") {
                    Text(
                        text = item.benefits,
                        style = MaterialTheme.typography.bodyLarge,
                        color = AguiaColors.TextPrimary
                    )
                }

                // Timeline
                if (item.timeline.isNotEmpty()) {
                    Section(title = "Acompanhamento") {
                        AguiaTimeline(events = item.timeline)
                    }
                }
            }
        } ?: run {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator()
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
            color = AguiaColors.TextPrimary
        )
        Spacer(modifier = Modifier.height(12.dp))
        content()
    }
}
