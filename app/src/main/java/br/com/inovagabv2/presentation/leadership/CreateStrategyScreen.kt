package br.com.inovagabv2.presentation.leadership

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.inovagabv2.core.designsystem.AguiaColors
import br.com.inovagabv2.core.designsystem.components.AguiaButton
import br.com.inovagabv2.core.designsystem.components.AguiaDateField
import br.com.inovagabv2.core.designsystem.components.AguiaTextField
import br.com.inovagabv2.core.designsystem.components.AguiaTopBar
import br.com.inovagabv2.presentation.session.rememberLoggedUserName

@Composable
fun CreateStrategyScreen(
    viewModel: CreateStrategyViewModel,
    onBackClick: () -> Unit,
    onSuccess: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.feedbackMessage) {
        val message = state.feedbackMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(message)
        if (state.isSuccess) onSuccess()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            AguiaTopBar(
                userName = rememberLoggedUserName(),
                title = "Nova Estratégia",
                onBackClick = onBackClick
            )
        },
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
            Text(
                text = "Cadastrar nova diretriz",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = AguiaColors.NavyDark
            )

            AguiaTextField(
                value = state.title,
                onValueChange = { viewModel.onTitleChange(it.replace("\n", " ")) },
                label = "Título da estratégia",
                singleLine = false,
                maxLines = 3
            )

            AguiaTextField(
                value = state.category,
                onValueChange = { viewModel.onCategoryChange(it.replace("\n", " ")) },
                label = "Categoria (ex: Operação, Sustentabilidade)",
                singleLine = false,
                maxLines = 3
            )

            AguiaTextField(
                value = state.campaign,
                onValueChange = { viewModel.onCampaignChange(it.replace("\n", " ")) },
                label = "Campanha",
                singleLine = false,
                maxLines = 3
            )

            AguiaDateField(
                digits = state.date,
                onDigitsChange = viewModel::onDateChange,
                label = "Data de Início (DD/MM/AAAA)",
                error = state.dateError
            )

            AguiaTextField(
                value = state.description,
                onValueChange = { viewModel.onDescriptionChange(it) },
                label = "Descrição detalhada",
                singleLine = false,
                minLines = 3,
                modifier = Modifier.heightIn(min = 100.dp)
            )

            if (!state.error.isNullOrBlank()) {
                Text(
                    text = state.error!!,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp
                )
            }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .imePadding()
                    .navigationBarsPadding()
            ) {
                OutlinedButton(
                    onClick = { viewModel.saveStrategy(activateNow = false) },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    enabled = state.isValid && !state.isSaving
                ) {
                    Text("Salvar Rascunho", fontWeight = FontWeight.Bold)
                }

                AguiaButton(
                    text = "Criar e Ativar",
                    onClick = { viewModel.saveStrategy(activateNow = true) },
                    modifier = Modifier.weight(1f),
                    isLoading = state.isSaving,
                    enabled = state.isValid && !state.isSaving
                )
            }
            Text(
                text = "Criar e Ativar publica a estratégia para os operadores.",
                fontSize = 12.sp,
                color = AguiaColors.TextSecondary,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
