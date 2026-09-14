package br.com.inovagabv2.presentation.manager.ideas.details

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
                    }
                }

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
