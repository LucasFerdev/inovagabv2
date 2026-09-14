package br.com.inovagabv2.presentation.leadership

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.inovagabv2.core.designsystem.AguiaColors
import br.com.inovagabv2.core.designsystem.components.AguiaButton
import br.com.inovagabv2.core.designsystem.components.AguiaTextField
import br.com.inovagabv2.core.designsystem.components.AguiaTopBar

@Composable
fun CreateStrategyScreen(
    viewModel: CreateStrategyViewModel,
    onBackClick: () -> Unit,
    onSuccess: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) onSuccess()
    }

    Scaffold(
        topBar = {
            AguiaTopBar(
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
                .fillMaxSize(),
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
                onValueChange = { viewModel.onTitleChange(it) },
                label = "Título da estratégia"
            )

            AguiaTextField(
                value = state.category,
                onValueChange = { viewModel.onCategoryChange(it) },
                label = "Categoria (ex: Operação, Sustentabilidade)"
            )

            AguiaTextField(
                value = state.campaign,
                onValueChange = { viewModel.onCampaignChange(it) },
                label = "Campanha"
            )

            AguiaTextField(
                value = state.date,
                onValueChange = { viewModel.onDateChange(it) },
                label = "Data de Início (AAAA-MM-DD)"
            )

            AguiaTextField(
                value = state.description,
                onValueChange = { viewModel.onDescriptionChange(it) },
                label = "Descrição detalhada",
                singleLine = false,
                modifier = Modifier.heightIn(min = 100.dp)
            )

            if (!state.error.isNullOrBlank()) {
                Text(
                    text = state.error!!,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
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
        }
    }
}
