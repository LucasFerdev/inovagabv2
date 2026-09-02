package br.com.inovagabv2.presentation.operator.strategy

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.com.inovagabv2.core.designsystem.AguiaColors
import br.com.inovagabv2.core.designsystem.components.AguiaTopBar

@Composable
fun StrategyDetailsScreen(
    viewModel: OperatorStrategyViewModel,
    strategyId: String,
    onBackClick: () -> Unit
) {
    val strategies by viewModel.filteredStrategies.collectAsState()
    val strategy = strategies.find { it.id == strategyId }

    Scaffold(
        topBar = {
            AguiaTopBar(
                title = "Detalhes da Diretriz",
                onBackClick = onBackClick
            )
        },
        containerColor = AguiaColors.Background
    ) { padding ->
        if (strategy == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Diretriz não encontrada")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = strategy.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = AguiaColors.TextPrimary
                    )
                }

                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = AguiaColors.CardWhite),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Descrição",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = AguiaColors.PrimaryBlue
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = strategy.description,
                                style = MaterialTheme.typography.bodyLarge,
                                color = AguiaColors.TextPrimary
                            )
                        }
                    }
                }

                item {
                    Text(
                        text = "Objetivos Principais",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AguiaColors.TextPrimary
                    )
                }

                items(strategy.objectives) { objective ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = AguiaColors.SuccessGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = objective,
                            style = MaterialTheme.typography.bodyMedium,
                            color = AguiaColors.TextPrimary
                        )
                    }
                }
            }
        }
    }
}
