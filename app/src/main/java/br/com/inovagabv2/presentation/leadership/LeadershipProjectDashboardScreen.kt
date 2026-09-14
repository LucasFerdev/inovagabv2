package br.com.inovagabv2.presentation.leadership

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
fun LeadershipProjectDashboardScreen(
    onBackClick: () -> Unit,
    viewModel: LeadershipProjectDashboardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            AguiaTopBar(
                title = "Dashboard do Projeto",
                onBackClick = onBackClick
            )
        },
        containerColor = Color.White
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                AguiaLoadingState(message = "Carregando indicadores do projeto...")
            }
        } else if (state.projectDetails == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                AguiaEmptyState(message = state.error ?: "Indicadores não encontrados.")
            }
        } else {
            val dash = state.projectDetails!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = dash.nome,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = AguiaColors.NavyDark
                )

                Text(
                    text = dash.descricao,
                    fontSize = 14.sp,
                    color = AguiaColors.TextSecondary
                )

                // Status & Atraso Pill Row
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AguiaStatusChip(statusText = dash.status.displayName)
                    if (dash.atrasado) {
                        Surface(shape = RoundedCornerShape(12.dp), color = Color(0xFFFEE2E2)) {
                            Text(
                                text = "ATRASADO",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFDC2626),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Financial Executive Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F2B5B))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text("Indicadores Financeiros", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(modifier = Modifier.height(14.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("Investimento", fontSize = 11.sp, color = Color.White.copy(alpha = 0.7f))
                                Text(formatMonetaryVal(dash.investimento.toDouble()), fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                            Column {
                                Text("Retorno", fontSize = 11.sp, color = Color.White.copy(alpha = 0.7f))
                                Text(formatMonetaryVal(dash.retornoFinanceiro?.toDouble() ?: 0.0), fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF34D399))
                            }
                            Column {
                                Text("ROI", fontSize = 11.sp, color = Color.White.copy(alpha = 0.7f))
                                Text("${String.format(java.util.Locale.US, "%.1f%%", dash.roiPercentual?.toDouble() ?: 0.0).replace(".", ",")}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8))
                            }
                        }
                    }
                }

                // Progress Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFEEF2F6))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Progresso Atual", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = AguiaColors.NavyDark)
                            Text("${dash.percentualProgresso}%", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = AguiaColors.PrimaryBlue)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        AguiaProgressBar(progress = dash.percentualProgresso / 100f, modifier = Modifier.fillMaxWidth())
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
