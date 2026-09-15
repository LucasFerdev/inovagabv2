package br.com.inovagabv2.presentation.operator.strategy

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.inovagabv2.core.designsystem.AguiaColors
import br.com.inovagabv2.core.designsystem.components.*

@Composable
fun StrategyDetailsScreen(
    onBackClick: () -> Unit,
    viewModel: StrategyDetailsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    if (state.showHistoryDialog) {
        AguiaHistoryDialog(
            historyItems = state.historyItems,
            isLoading = state.isLoadingHistory,
            onDismiss = viewModel::onDismissHistoryDialog,
            title = "Histórico da Estratégia"
        )
    }

    Scaffold(
        topBar = {
            AguiaTopBar(
                title = "Detalhes da Diretriz",
                onBackClick = onBackClick
            )
        },
        containerColor = AguiaColors.Background
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                AguiaLoadingState(message = "Carregando diretriz...")
            }
        } else if (state.strategy == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                AguiaEmptyState(message = state.error ?: "Diretriz não encontrada.")
            }
        } else {
            val strategy = state.strategy!!
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = strategy.title,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = AguiaColors.NavyDark,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        AguiaStatusChip(statusText = strategy.status.displayName)
                    }
                }

                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = AguiaColors.CardWhite),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
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
                                color = AguiaColors.NavyDark,
                                lineHeight = 20.sp
                            )
                            strategy.category?.let { cat ->
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("Categoria: $cat", fontSize = 13.sp, color = AguiaColors.TextSecondary)
                            }
                            strategy.campaign?.let { camp ->
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Campanha: $camp", fontSize = 13.sp, color = AguiaColors.TextSecondary)
                            }
                            val formattedDate = formatDatePtBr(strategy.publishedDate ?: strategy.date)
                            if (formattedDate.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Data: $formattedDate", fontSize = 13.sp, color = AguiaColors.TextSecondary)
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            TextButton(
                                onClick = viewModel::onShowHistoryDialog,
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text("Ver histórico de alterações", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AguiaColors.PrimaryBlue)
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun formatDatePtBr(isoDate: String?): String {
    if (isoDate.isNullOrBlank()) return ""
    return try {
        val parts = isoDate.split("-")
        if (parts.size >= 3) {
            "${parts[2].take(2)}/${parts[1]}/${parts[0]}"
        } else isoDate
    } catch (_: Exception) {
        isoDate
    }
}
