package br.com.inovagabv2.presentation.leadership

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
import br.com.inovagabv2.domain.model.StrategyStatus

@Composable
fun EditStrategyScreen(
    onBackClick: () -> Unit,
    viewModel: EditStrategyViewModel = hiltViewModel()
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
            title = "Histórico da Estratégia"
        )
    }

    if (state.showArchiveDialog) {
        AlertDialog(
            onDismissRequest = viewModel::onDismissArchiveDialog,
            title = { Text("Arquivar Estratégia", fontWeight = FontWeight.Bold) },
            text = { Text("Deseja arquivar esta estratégia? A ação alterará o status para arquivada.") },
            confirmButton = {
                Button(
                    onClick = viewModel::onArchiveStrategy,
                    colors = ButtonDefaults.buttonColors(containerColor = AguiaColors.ErrorRed)
                ) {
                    Text("Confirmar Arquivamento", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::onDismissArchiveDialog) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            AguiaTopBar(
                title = "Editar Estratégia",
                onBackClick = onBackClick
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.White
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                AguiaLoadingState(message = "Carregando estratégia...")
            }
        } else if (state.strategy == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                AguiaEmptyState(message = state.errorMessage ?: "Estratégia não encontrada.")
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(20.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Editar diretriz",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = AguiaColors.NavyDark
                    )
                    AguiaStatusChip(statusText = state.status.displayName)
                }

                AguiaTextField(
                    value = state.title,
                    onValueChange = viewModel::onTitleChange,
                    label = "Título da estratégia"
                )

                AguiaTextField(
                    value = state.category,
                    onValueChange = viewModel::onCategoryChange,
                    label = "Categoria"
                )

                AguiaTextField(
                    value = state.campaign,
                    onValueChange = viewModel::onCampaignChange,
                    label = "Campanha"
                )

                AguiaTextField(
                    value = state.date,
                    onValueChange = viewModel::onDateChange,
                    label = "Data (AAAA-MM-DD)"
                )

                AguiaTextField(
                    value = state.description,
                    onValueChange = viewModel::onDescriptionChange,
                    label = "Descrição detalhada",
                    singleLine = false,
                    modifier = Modifier.heightIn(min = 100.dp)
                )

                TextButton(
                    onClick = viewModel::onShowHistoryDialog,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Ver histórico de alterações", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AguiaColors.PrimaryBlue)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Action buttons
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    AguiaButton(
                        text = "Salvar Alterações",
                        onClick = viewModel::onUpdateStrategy,
                        modifier = Modifier.fillMaxWidth(),
                        isLoading = state.isSaving,
                        enabled = state.isValid && !state.isSaving
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (state.status == StrategyStatus.ATIVA) {
                            OutlinedButton(
                                onClick = viewModel::onDeactivateStrategy,
                                modifier = Modifier.weight(1f).height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                enabled = !state.isSaving
                            ) {
                                Text("Desativar", fontWeight = FontWeight.Bold)
                            }
                        } else if (state.status == StrategyStatus.RASCUNHO || state.status == StrategyStatus.INATIVA) {
                            Button(
                                onClick = viewModel::onActivateStrategy,
                                modifier = Modifier.weight(1f).height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AguiaColors.SuccessGreen),
                                enabled = !state.isSaving
                            ) {
                                Text("Ativar", fontWeight = FontWeight.Bold)
                            }
                        }

                        OutlinedButton(
                            onClick = viewModel::onShowArchiveDialog,
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, AguiaColors.ErrorRed),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = AguiaColors.ErrorRed),
                            enabled = !state.isSaving
                        ) {
                            Text("Arquivar", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
