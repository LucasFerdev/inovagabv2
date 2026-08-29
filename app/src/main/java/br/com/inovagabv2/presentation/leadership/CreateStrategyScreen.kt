package br.com.inovagabv2.presentation.leadership

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
                title = "Nova Diretriz",
                onBackClick = onBackClick
            )
        },
        containerColor = AguiaColors.Background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            LinearProgressIndicator(
                progress = { (state.currentStep + 1) / 3f },
                modifier = Modifier.fillMaxWidth(),
                color = AguiaColors.PrimaryBlue
            )
            Spacer(modifier = Modifier.height(24.dp))

            when (state.currentStep) {
                0 -> StepContent(viewModel)
                1 -> StepObjectives(viewModel)
                2 -> StepReview(viewModel)
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                if (state.currentStep > 0) {
                    OutlinedButton(
                        onClick = { viewModel.onPreviousStep() },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Anterior")
                    }
                }
                
                AguiaButton(
                    text = if (state.currentStep == 2) "Publicar" else "Próximo",
                    onClick = { 
                        if (state.currentStep == 2) viewModel.saveStrategy(true) else viewModel.onNextStep() 
                    },
                    modifier = Modifier.weight(1f),
                    isLoading = state.isSaving
                )
            }
        }
    }
}

@Composable
private fun StepContent(viewModel: CreateStrategyViewModel) {
    val state by viewModel.state.collectAsState()
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Conteúdo da Diretriz", style = MaterialTheme.typography.titleMedium)
        AguiaTextField(
            value = state.title,
            onValueChange = { viewModel.onTitleChange(it) },
            label = "Título da Diretriz"
        )
        AguiaTextField(
            value = state.description,
            onValueChange = { viewModel.onDescriptionChange(it) },
            label = "Descrição Geral",
            singleLine = false,
            modifier = Modifier.height(150.dp)
        )
    }
}

@Composable
private fun StepObjectives(viewModel: CreateStrategyViewModel) {
    val state by viewModel.state.collectAsState()
    var newObjective by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Objetivos Principais", style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AguiaTextField(
                value = newObjective,
                onValueChange = { newObjective = it },
                label = "Novo Objetivo",
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = { 
                    if (newObjective.isNotBlank()) {
                        viewModel.addObjective(newObjective)
                        newObjective = ""
                    }
                },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = AguiaColors.PrimaryBlue)
            }
        }
        
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(state.objectives) { objective ->
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(8.dp),
                    tonalElevation = 2.dp
                ) {
                    Row(modifier = Modifier.padding(12.dp).fillMaxWidth()) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = AguiaColors.SuccessGreen, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(objective)
                    }
                }
            }
        }
    }
}

@Composable
private fun StepReview(viewModel: CreateStrategyViewModel) {
    val state by viewModel.state.collectAsState()
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Revisão Final", style = MaterialTheme.typography.titleMedium)
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                Text(state.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(state.description, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Objetivos:", fontWeight = FontWeight.Bold)
                state.objectives.forEach {
                    Text("• $it")
                }
            }
        }
    }
}
