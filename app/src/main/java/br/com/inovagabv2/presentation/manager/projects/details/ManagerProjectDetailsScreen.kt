package br.com.inovagabv2.presentation.manager.projects.details

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
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
import br.com.inovagabv2.domain.model.ProjectStage
import br.com.inovagabv2.domain.model.ProjectStatus
import java.math.BigDecimal

@Composable
fun ManagerProjectDetailsScreen(
    onBackClick: () -> Unit,
    viewModel: ManagerProjectDetailsViewModel = hiltViewModel()
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

    // Progress Dialog
    if (state.showProgressDialog && state.project != null) {
        val proj = state.project!!
        var selectedStage by remember { mutableStateOf(proj.stage) }
        var selectedStatus by remember { mutableStateOf(proj.status) }
        var progressVal by remember { mutableFloatStateOf(proj.percentualProgresso.toFloat()) }
        var justification by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = viewModel::onDismissProgressDialog,
            title = { Text("Atualizar Progresso", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Etapa do Projeto:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        ProjectStage.entries.forEach { st ->
                            FilterChip(
                                selected = selectedStage == st,
                                onClick = { selectedStage = st },
                                label = { Text(st.displayName, fontSize = 10.sp) }
                            )
                        }
                    }

                    Text("Status do Projeto:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf(ProjectStatus.PLANEJADO, ProjectStatus.EM_ANDAMENTO, ProjectStatus.PAUSADO).forEach { st ->
                            FilterChip(
                                selected = selectedStatus == st,
                                onClick = { selectedStatus = st },
                                label = { Text(st.displayName, fontSize = 10.sp) }
                            )
                        }
                    }

                    Text("Progresso (${progressVal.toInt()}%):", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Slider(
                        value = progressVal,
                        onValueChange = { progressVal = it },
                        valueRange = 0f..100f
                    )
                    if (progressVal.toInt() < proj.percentualProgresso) {
                        OutlinedTextField(
                            value = justification,
                            onValueChange = { justification = it },
                            placeholder = { Text("Justificativa da redução de progresso...") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.onUpdateProgress(selectedStage, selectedStatus, progressVal.toInt(), justification.ifBlank { null })
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AguiaColors.PrimaryBlue)
                ) {
                    Text("Salvar Progresso", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::onDismissProgressDialog) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Results Dialog
    if (state.showResultsDialog) {
        var returnValStr by remember { mutableStateOf("") }
        var productivityStr by remember { mutableStateOf("") }
        var resultText by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = viewModel::onDismissResultsDialog,
            title = { Text("Registrar Resultados", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = returnValStr,
                        onValueChange = { returnValStr = it },
                        label = { Text("Retorno Financeiro (R$)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = productivityStr,
                        onValueChange = { productivityStr = it },
                        label = { Text("Ganho de Produtividade (%)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = resultText,
                        onValueChange = { resultText = it },
                        label = { Text("Resumo do Resultado") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val retVal = BigDecimal(returnValStr.replace(",", ".").toDoubleOrNull() ?: 0.0)
                        val prodVal = BigDecimal(productivityStr.replace(",", ".").toDoubleOrNull() ?: 0.0)
                        viewModel.onRegisterResults(retVal, prodVal, resultText)
                    },
                    enabled = resultText.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = AguiaColors.SuccessGreen)
                ) {
                    Text("Salvar Resultados", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::onDismissResultsDialog) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Conclude Confirm Dialog
    if (state.showConcludeDialog) {
        AlertDialog(
            onDismissRequest = viewModel::onDismissConcludeDialog,
            title = { Text("Concluir Projeto", fontWeight = FontWeight.Bold) },
            text = { Text("Deseja marcar este projeto como concluído?") },
            confirmButton = {
                Button(
                    onClick = viewModel::onConcludeProject,
                    colors = ButtonDefaults.buttonColors(containerColor = AguiaColors.SuccessGreen)
                ) {
                    Text("Confirmar Conclusão", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = { TextButton(onClick = viewModel::onDismissConcludeDialog) { Text("Cancelar") } }
        )
    }

    // History Dialog
    if (state.showHistoryDialog) {
        AguiaHistoryDialog(
            historyItems = state.historyItems,
            isLoading = state.isLoadingHistory,
            onDismiss = viewModel::onDismissHistoryDialog,
            title = "Histórico do Projeto"
        )
    }

    // Cancel Confirm Dialog
    if (state.showCancelDialog) {
        AlertDialog(
            onDismissRequest = viewModel::onDismissCancelDialog,
            title = { Text("Cancelar Projeto", fontWeight = FontWeight.Bold) },
            text = { Text("Deseja cancelar este projeto? A ação é irreversível.") },
            confirmButton = {
                Button(
                    onClick = viewModel::onCancelProject,
                    colors = ButtonDefaults.buttonColors(containerColor = AguiaColors.ErrorRed)
                ) {
                    Text("Confirmar Cancelamento", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = { TextButton(onClick = viewModel::onDismissCancelDialog) { Text("Voltar") } }
        )
    }

    Scaffold(
        topBar = {
            AguiaTopBar(
                title = "Detalhes do Projeto",
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
        } else if (state.project != null) {
            val project = state.project!!
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
                                text = "Etapa: ${project.stage.displayName}",
                                style = MaterialTheme.typography.labelSmall,
                                color = AguiaColors.PrimaryBlue
                            )
                            AguiaStatusChip(statusText = project.status.displayName)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = project.name,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = AguiaColors.TextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = project.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = AguiaColors.TextSecondary
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        TextButton(
                            onClick = viewModel::onShowHistoryDialog,
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Ver histórico de alterações", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AguiaColors.PrimaryBlue)
                        }
                    }
                }

                // Progress Section Card
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
                                color = AguiaColors.TextPrimary
                            )
                            Text(
                                text = "${project.percentualProgresso}%",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = AguiaColors.PrimaryBlue
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        AguiaProgressBar(
                            progress = project.progress,
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (!project.isReadOnly) {
                            Spacer(modifier = Modifier.height(16.dp))
                            AguiaButton(
                                text = "Atualizar progresso",
                                onClick = viewModel::onShowProgressDialog,
                                outline = true,
                                containerColor = AguiaColors.ManagerPurple,
                                isLoading = state.isSubmitting
                            )
                        }
                    }
                }

                // Details Row Cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AguiaMetricCard(
                        label = "Prazo",
                        value = project.deadline,
                        color = AguiaColors.TextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    AguiaMetricCard(
                        label = "Investimento",
                        value = formatMonetaryVal(project.investment.toDouble()),
                        color = AguiaColors.TextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Results Section Card
                if (project.resultado != null || project.retornoFinanceiro != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = AguiaColors.CardWhite)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Resultados Alcançados",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = AguiaColors.NavyDark
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            project.retornoFinanceiro?.let { ret ->
                                Text("Retorno Financeiro: ${formatMonetaryVal(ret.toDouble())}", fontSize = 13.sp, color = Color(0xFF16A34A), fontWeight = FontWeight.Bold)
                            }
                            project.ganhoProdutividadePercentual?.let { prod ->
                                Text("Ganho de Produtividade: ${prod.toDouble()}%", fontSize = 13.sp, color = AguiaColors.PrimaryBlue)
                            }
                            project.resultado?.let { res ->
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(res, fontSize = 13.sp, color = AguiaColors.TextSecondary)
                            }
                        }
                    }
                }

                // Administration Action Buttons (Gestor)
                if (!project.isReadOnly) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        AguiaButton(
                            text = "Registrar Resultados",
                            onClick = viewModel::onShowResultsDialog,
                            modifier = Modifier.fillMaxWidth(),
                            containerColor = AguiaColors.PrimaryBlue,
                            isLoading = state.isSubmitting
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            AguiaButton(
                                text = "Concluir Projeto",
                                onClick = viewModel::onShowConcludeDialog,
                                modifier = Modifier.weight(1f),
                                containerColor = AguiaColors.SuccessGreen,
                                isLoading = state.isSubmitting
                            )

                            AguiaButton(
                                text = "Cancelar",
                                onClick = viewModel::onShowCancelDialog,
                                modifier = Modifier.weight(1f),
                                outline = true,
                                containerColor = AguiaColors.ErrorRed,
                                isLoading = state.isSubmitting
                            )
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
