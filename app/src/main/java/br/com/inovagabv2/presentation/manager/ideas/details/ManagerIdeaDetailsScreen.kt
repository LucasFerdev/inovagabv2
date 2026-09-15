package br.com.inovagabv2.presentation.manager.ideas.details

import androidx.compose.foundation.BorderStroke
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
import br.com.inovagabv2.domain.model.IdeaStatus

@Composable
fun ManagerIdeaDetailsScreen(
    onBackClick: () -> Unit,
    viewModel: ManagerIdeaDetailsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.shouldNavigateBack) {
        if (state.shouldNavigateBack) {
            onBackClick()
        }
    }

    LaunchedEffect(state.feedbackMessage) {
        state.feedbackMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearFeedbackMessage()
        }
    }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearErrorMessage()
        }
    }

    // History Dialog
    if (state.showHistoryDialog) {
        AguiaHistoryDialog(
            historyItems = state.historyItems,
            isLoading = state.isLoadingHistory,
            onDismiss = viewModel::onDismissHistoryDialog,
            title = "Histórico da Ideia"
        )
    }

    // Approval Confirmation Dialog
    if (state.showApprovalDialog) {
        AlertDialog(
            onDismissRequest = viewModel::onDismissApprovalDialog,
            title = { Text("Confirmar Aprovação", fontWeight = FontWeight.Bold) },
            text = { Text("Deseja aprovar esta ideia com prioridade P${state.selectedPriority ?: 3}?") },
            confirmButton = {
                Button(
                    onClick = viewModel::onConfirmApproval,
                    colors = ButtonDefaults.buttonColors(containerColor = AguiaColors.SuccessGreen)
                ) {
                    Text("Confirmar e Aprovar", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::onDismissApprovalDialog) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Rejection Confirmation Dialog with mandatory reason field
    if (state.showRejectionDialog) {
        AlertDialog(
            onDismissRequest = viewModel::onDismissRejectionDialog,
            title = { Text("Confirmar Rejeição", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Por favor, informe a justificativa da rejeição:")
                    OutlinedTextField(
                        value = state.rejectionReason,
                        onValueChange = viewModel::onRejectionReasonChange,
                        placeholder = { Text("Motivo da rejeição...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = viewModel::onConfirmRejection,
                    colors = ButtonDefaults.buttonColors(containerColor = AguiaColors.ErrorRed),
                    enabled = state.rejectionReason.trim().length >= 3 && !state.isSubmitting
                ) {
                    Text("Confirmar e Rejeitar", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::onDismissRejectionDialog) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Recalculate AI Confirmation Dialog
    if (state.showRecalculateDialog) {
        AlertDialog(
            onDismissRequest = viewModel::onDismissRecalculateDialog,
            title = { Text("Recalcular Análise da IA", fontWeight = FontWeight.Bold) },
            text = { Text("Deseja gerar uma nova análise consultiva com a IA para esta ideia?") },
            confirmButton = {
                Button(
                    onClick = viewModel::onConfirmRecalculateAi,
                    colors = ButtonDefaults.buttonColors(containerColor = AguiaColors.PrimaryBlue)
                ) {
                    Text("Confirmar e Recalcular", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::onDismissRecalculateDialog) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            AguiaTopBar(
                title = "Detalhes da Ideia",
                onBackClick = onBackClick,
                backgroundColor = AguiaColors.ManagerPurple,
                contentColor = AguiaColors.CardWhite
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = AguiaColors.Background
    ) { padding ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AguiaColors.ManagerPurple)
            }
        } else if (state.idea != null) {
            val idea = state.idea!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Card 1: Main Idea Info
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
                                text = idea.category.uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                                color = AguiaColors.PrimaryBlue
                            )
                            AguiaStatusChip(status = idea.status)
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Text(
                            text = idea.title,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = AguiaColors.TextPrimary
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "Problema identificado",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = AguiaColors.TextPrimary
                        )
                        Text(
                            text = idea.problem.ifBlank { idea.description },
                            style = MaterialTheme.typography.bodyMedium,
                            color = AguiaColors.TextSecondary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Solução proposta",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = AguiaColors.TextPrimary
                        )
                        Text(
                            text = idea.proposedSolution.ifBlank { idea.description },
                            style = MaterialTheme.typography.bodyMedium,
                            color = AguiaColors.TextSecondary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Benefícios esperados",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = AguiaColors.TextPrimary
                        )
                        Text(
                            text = idea.expectedBenefits,
                            style = MaterialTheme.typography.bodyMedium,
                            color = AguiaColors.TextSecondary
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "Autor",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = AguiaColors.TextPrimary
                        )
                        Text(
                            text = idea.authorName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = AguiaColors.TextSecondary
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        TextButton(
                            onClick = viewModel::onShowHistoryDialog,
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Ver histórico de alterações", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AguiaColors.PrimaryBlue)
                        }
                    }
                }

                // Card 2: AI Analysis Section (Gestor)
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
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "A análise da IA é uma recomendação. A decisão final pertence ao Gestor.",
                            fontSize = 11.sp,
                            color = AguiaColors.TextSecondary
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        if (state.isLoadingAi) {
                            Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = AguiaColors.PrimaryBlue)
                            }
                        } else if (state.aiAnalysis != null) {
                            val ai = state.aiAnalysis!!

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFFEFF6FF)
                                ) {
                                    Text(
                                        text = "Pontuação: ${ai.overallScore}/100",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AguiaColors.PrimaryBlue,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFFFEF3C7)
                                ) {
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

                            Text(
                                text = "Resumo executivo:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = AguiaColors.NavyDark
                            )
                            Text(
                                text = ai.executiveSummary,
                                fontSize = 13.sp,
                                color = AguiaColors.TextSecondary,
                                lineHeight = 18.sp
                            )

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

                            Spacer(modifier = Modifier.height(14.dp))

                            OutlinedButton(
                                onClick = viewModel::onShowRecalculateDialog,
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !state.isSubmittingAi
                            ) {
                                if (state.isSubmittingAi) {
                                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                } else {
                                    Text("Recalcular Análise da IA", fontWeight = FontWeight.Bold)
                                }
                            }
                        } else {
                            Text(
                                text = "Esta ideia ainda não possui análise da IA.",
                                fontSize = 13.sp,
                                color = AguiaColors.TextSecondary
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            AguiaButton(
                                text = "Analisar com IA",
                                onClick = viewModel::onAnalyzeAi,
                                modifier = Modifier.fillMaxWidth(),
                                isLoading = state.isSubmittingAi,
                                enabled = !state.isSubmittingAi
                            )
                        }
                    }
                }

                // Card 3: Evaluation Actions (Gestor)
                if (idea.status == IdeaStatus.ENVIADA || idea.status == IdeaStatus.EM_ANALISE) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = AguiaColors.CardWhite)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Avaliação da Gestão",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = AguiaColors.TextPrimary
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            if (idea.status == IdeaStatus.ENVIADA) {
                                AguiaButton(
                                    text = "Colocar em Análise",
                                    onClick = viewModel::onAnalisar,
                                    modifier = Modifier.fillMaxWidth(),
                                    containerColor = AguiaColors.PrimaryBlue,
                                    isLoading = state.isSubmitting
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                            
                            AguiaPrioritySelector(
                                selectedPriority = state.selectedPriority,
                                onPrioritySelected = viewModel::onPrioritySelected
                            )
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                AguiaButton(
                                    text = "Aprovar Ideia",
                                    onClick = viewModel::onShowApprovalDialog,
                                    modifier = Modifier.weight(1f),
                                    containerColor = AguiaColors.SuccessGreen,
                                    isLoading = state.isSubmitting,
                                    enabled = state.selectedPriority != null && !state.isSubmitting
                                )
                                AguiaButton(
                                    text = "Rejeitar",
                                    onClick = viewModel::onShowRejectionDialog,
                                    modifier = Modifier.weight(1f),
                                    outline = true,
                                    containerColor = AguiaColors.ErrorRed,
                                    isLoading = state.isSubmitting,
                                    enabled = !state.isSubmitting
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
