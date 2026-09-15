package br.com.inovagabv2.presentation.operator.details

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.inovagabv2.core.designsystem.AguiaColors
import br.com.inovagabv2.core.designsystem.components.*
import br.com.inovagabv2.domain.model.Role

@Composable
fun IdeaDetailsScreen(
    viewModel: IdeaDetailsViewModel,
    onBackClick: () -> Unit
) {
    val idea by viewModel.idea.collectAsState()
    val user by viewModel.user.collectAsState()
    val aiAnalysis by viewModel.aiAnalysis.collectAsState()
    val isLoadingAi by viewModel.isLoadingAi.collectAsState()
    val aiError by viewModel.aiError.collectAsState()

    val historyItems by viewModel.historyItems.collectAsState()
    val isLoadingHistory by viewModel.isLoadingHistory.collectAsState()
    val showHistoryDialog by viewModel.showHistoryDialog.collectAsState()

    val currentRole = user?.role ?: Role.OPERADOR

    if (showHistoryDialog) {
        AguiaHistoryDialog(
            historyItems = historyItems,
            isLoading = isLoadingHistory,
            onDismiss = viewModel::onDismissHistoryDialog,
            title = "Histórico da Ideia"
        )
    }

    Scaffold(
        topBar = {
            AguiaTopBar(
                userName = user?.name,
                role = currentRole,
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = AguiaColors.NavyDark,
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        AguiaStatusChip(status = item.status)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Date & Folder Category Row
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (!item.createdAt.isNullOrBlank()) "Enviada em ${item.createdAt}" else "",
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

                    Spacer(modifier = Modifier.height(10.dp))
                    TextButton(
                        onClick = viewModel::onShowHistoryDialog,
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Ver histórico de alterações", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AguiaColors.PrimaryBlue)
                    }
                }

                HorizontalDivider(color = AguiaColors.TextSecondary.copy(alpha = 0.15f))

                // Description
                Section(title = "Problema identificado") {
                    Text(
                        text = item.problem.ifBlank { item.description },
                        style = MaterialTheme.typography.bodyMedium,
                        color = AguiaColors.NavyDark,
                        lineHeight = 22.sp
                    )
                }

                Section(title = "Solução proposta") {
                    Text(
                        text = item.proposedSolution.ifBlank { item.description },
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

                // Read-only AI Section for Leadership / Gestor
                if (currentRole == Role.LIDERANCA || currentRole == Role.GESTOR) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Análise da Inteligência Artificial",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = AguiaColors.NavyDark
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            if (isLoadingAi) {
                                Box(modifier = Modifier.fillMaxWidth().padding(12.dp), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = AguiaColors.PrimaryBlue)
                                }
                            } else if (aiAnalysis != null) {
                                val ai = aiAnalysis!!
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Surface(shape = RoundedCornerShape(12.dp), color = Color(0xFFEFF6FF)) {
                                        Text(
                                            text = "Pontuação: ${ai.overallScore}/100",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AguiaColors.PrimaryBlue,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }

                                    Surface(shape = RoundedCornerShape(12.dp), color = Color(0xFFFEF3C7)) {
                                        Text(
                                            text = "Prioridade sugerida: P${ai.suggestedPriority}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFD97706),
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(text = "Resumo executivo:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AguiaColors.NavyDark)
                                Text(text = ai.executiveSummary, fontSize = 13.sp, color = AguiaColors.TextSecondary, lineHeight = 18.sp)

                                if (ai.strengths.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(text = "Pontos fortes:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                                    ai.strengths.forEach { Text(text = "• $it", fontSize = 12.sp, color = AguiaColors.TextSecondary) }
                                }

                                if (ai.risks.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(text = "Riscos:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFDC2626))
                                    ai.risks.forEach { Text(text = "• $it", fontSize = 12.sp, color = AguiaColors.TextSecondary) }
                                }

                                if (ai.recommendations.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(text = "Recomendações:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AguiaColors.PrimaryBlue)
                                    ai.recommendations.forEach { Text(text = "• $it", fontSize = 12.sp, color = AguiaColors.TextSecondary) }
                                }
                            } else {
                                Text(
                                    text = aiError ?: "Esta ideia ainda não possui análise da IA.",
                                    fontSize = 13.sp,
                                    color = AguiaColors.TextSecondary
                                )
                            }
                        }
                    }
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
