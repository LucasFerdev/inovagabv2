package br.com.inovagabv2.presentation.manager.projects.create

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
fun ManagerCreateProjectScreen(
    onBackClick: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: ManagerCreateProjectViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onSuccess()
        }
    }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    Scaffold(
        topBar = {
            AguiaTopBar(
                title = "Novo Projeto",
                onBackClick = onBackClick,
                backgroundColor = AguiaColors.ManagerPurple,
                contentColor = AguiaColors.CardWhite
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Cadastrar projeto de inovação",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = AguiaColors.NavyDark
            )

            AguiaTextField(
                value = state.name,
                onValueChange = viewModel::onNameChange,
                label = "Nome do projeto"
            )

            AguiaTextField(
                value = state.description,
                onValueChange = viewModel::onDescriptionChange,
                label = "Descrição detalhada",
                singleLine = false,
                modifier = Modifier.heightIn(min = 90.dp)
            )

            // Select Strategy
            Text(
                text = "Estratégia Vinculada *",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = AguiaColors.NavyDark
            )

            if (state.isLoadingStrategies) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = AguiaColors.PrimaryBlue)
            } else if (state.activeStrategies.isEmpty()) {
                Text(
                    text = "Nenhuma estratégia ativa encontrada.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(state.activeStrategies) { strategy ->
                        val isSelected = state.selectedStrategy?.id == strategy.id
                        Surface(
                            modifier = Modifier.clickable { viewModel.onStrategySelected(strategy) },
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) AguiaColors.PrimaryBlue else Color.White,
                            border = if (isSelected) null else BorderStroke(1.dp, Color(0xFF3B82F6))
                        ) {
                            Text(
                                text = strategy.title,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else AguiaColors.PrimaryBlue,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // Optional Ideia Origem
            val matchingIdeas = state.approvedIdeas.filter { it.strategyId == state.selectedStrategy?.id }
            if (matchingIdeas.isNotEmpty()) {
                Text(
                    text = "Ideia de Origem (opcional)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = AguiaColors.NavyDark
                )

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        val isNone = state.selectedIdea == null
                        Surface(
                            modifier = Modifier.clickable { viewModel.onIdeaSelected(null) },
                            shape = RoundedCornerShape(20.dp),
                            color = if (isNone) AguiaColors.NavyDark else Color.White,
                            border = if (isNone) null else BorderStroke(1.dp, Color(0xFF94A3B8))
                        ) {
                            Text(
                                text = "Nenhuma",
                                fontSize = 12.sp,
                                color = if (isNone) Color.White else AguiaColors.NavyDark,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            )
                        }
                    }

                    items(matchingIdeas) { idea ->
                        val isSelected = state.selectedIdea?.id == idea.id
                        Surface(
                            modifier = Modifier.clickable { viewModel.onIdeaSelected(idea) },
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) AguiaColors.SuccessGreen else Color.White,
                            border = if (isSelected) null else BorderStroke(1.dp, Color(0xFF10B981))
                        ) {
                            Text(
                                text = idea.title,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else Color(0xFF16A34A),
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            AguiaTextField(
                value = state.investmentStr,
                onValueChange = viewModel::onInvestmentChange,
                label = "Investimento Estimado (R$)"
            )

            AguiaTextField(
                value = state.deadline,
                onValueChange = viewModel::onDeadlineChange,
                label = "Prazo Estimado (AAAA-MM-DD)"
            )

            Spacer(modifier = Modifier.height(16.dp))

            AguiaButton(
                text = "Criar Projeto",
                onClick = viewModel::submitProject,
                modifier = Modifier.fillMaxWidth(),
                isLoading = state.isSaving,
                enabled = state.isValid && !state.isSaving
            )
        }
    }
}
