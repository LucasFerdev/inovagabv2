package br.com.inovagabv2.presentation.leadership

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.inovagabv2.core.designsystem.AguiaColors
import br.com.inovagabv2.core.designsystem.components.*

@Composable
fun LeadershipProjectDetailsScreen(
    onBackClick: () -> Unit,
    viewModel: LeadershipProjectDetailsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            AguiaTopBar(
                title = "Detalhes do Projeto",
                onBackClick = onBackClick
            )
        },
        containerColor = AguiaColors.Background
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                AguiaLoadingState(message = "Carregando detalhes do projeto...")
            }
        } else if (state.project == null && state.dashboardDetails == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                AguiaEmptyState(message = state.error ?: "Projeto não encontrado.")
            }
        } else {
            val proj = state.project
            val dash = state.dashboardDetails

            val name = dash?.nome ?: proj?.name ?: ""
            val desc = dash?.descricao ?: proj?.description ?: ""
            val stageName = dash?.etapa?.displayName ?: proj?.stage?.displayName ?: ""
            val statusName = dash?.status?.displayName ?: proj?.status?.displayName ?: ""
            val progressPercent = dash?.percentualProgresso ?: proj?.percentualProgresso ?: 0
            val deadline = dash?.prazo ?: proj?.deadline ?: ""

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header Info Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = AguiaColors.CardWhite)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Etapa: $stageName",
                                style = MaterialTheme.typography.labelSmall,
                                color = AguiaColors.PrimaryBlue
                            )
                            AguiaStatusChip(statusText = statusName)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = name,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = AguiaColors.NavyDark
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = desc,
                            style = MaterialTheme.typography.bodyMedium,
                            color = AguiaColors.TextSecondary
                        )

                        dash?.estrategiaTitulo?.let { estTitle ->
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("Estratégia: $estTitle", fontSize = 12.sp, color = AguiaColors.PrimaryBlue, fontWeight = FontWeight.Medium)
                        }

                        dash?.ideiaOrigemTitulo?.let { ideaTitle ->
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Ideia de Origem: $ideaTitle", fontSize = 12.sp, color = Color(0xFF16A34A), fontWeight = FontWeight.Medium)
                        }
                    }
                }

                // Progress Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = AguiaColors.CardWhite)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Progresso Atual",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = AguiaColors.NavyDark
                            )
                            Text(
                                text = "$progressPercent%",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = AguiaColors.PrimaryBlue
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        AguiaProgressBar(
                            progress = progressPercent / 100f,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Financial & KPI Cards
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = AguiaColors.CardWhite)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Indicadores Financeiros", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = AguiaColors.NavyDark)
                        Text("Prazo: $deadline", fontSize = 13.sp, color = AguiaColors.TextSecondary)

                        dash?.investimento?.let { inv ->
                            Text("Investimento: ${formatMonetaryVal(inv.toDouble())}", fontSize = 13.sp, color = AguiaColors.NavyDark, fontWeight = FontWeight.SemiBold)
                        }
                        dash?.retornoFinanceiro?.let { ret ->
                            Text("Retorno Financeiro: ${formatMonetaryVal(ret.toDouble())}", fontSize = 13.sp, color = Color(0xFF16A34A), fontWeight = FontWeight.Bold)
                        }
                        dash?.lucro?.let { luc ->
                            Text("Lucro: ${formatMonetaryVal(luc.toDouble())}", fontSize = 13.sp, color = Color(0xFF16A34A), fontWeight = FontWeight.Bold)
                        }
                        dash?.roiPercentual?.let { roi ->
                            Text("ROI: ${String.format(java.util.Locale.US, "%.1f%%", roi.toDouble()).replace(".", ",")}", fontSize = 13.sp, color = AguiaColors.PrimaryBlue, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

private fun formatMonetaryVal(value: Double): String {
    return when {
        value >= 1_000_000 -> String.format(java.util.Locale.US, "R$ %.2f mi", value / 1_000_000).replace(".", ",")
        value >= 1_000 -> String.format(java.util.Locale.US, "R$ %.0f mil", value / 1_000)
        else -> String.format(java.util.Locale.US, "R$ %.2f", value).replace(".", ",")
    }
}
