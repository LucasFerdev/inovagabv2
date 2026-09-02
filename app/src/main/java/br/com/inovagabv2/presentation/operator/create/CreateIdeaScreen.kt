package br.com.inovagabv2.presentation.operator.create

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.com.inovagabv2.core.designsystem.AguiaColors
import br.com.inovagabv2.core.designsystem.components.AguiaButton
import br.com.inovagabv2.core.designsystem.components.AguiaTextField
import br.com.inovagabv2.core.designsystem.components.AguiaTopBar

@Composable
fun CreateIdeaScreen(
    viewModel: CreateIdeaViewModel,
    onBackClick: () -> Unit,
    onSuccess: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onSuccess()
        }
    }

    Scaffold(
        topBar = {
            AguiaTopBar(
                title = "Nova sugestão",
                onBackClick = {
                    if (state.currentStep > 1) viewModel.previousStep()
                    else onBackClick()
                }
            )
        },
        containerColor = AguiaColors.Background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            StepIndicator(currentStep = state.currentStep)
            
            Spacer(modifier = Modifier.height(24.dp))

            Box(modifier = Modifier.weight(1f)) {
                when (state.currentStep) {
                    1 -> CategoryStep(
                        selectedCategory = state.category,
                        onCategorySelected = viewModel::onCategoryChange
                    )
                    2 -> DetailsStep(
                        title = state.title,
                        description = state.description,
                        benefits = state.benefits,
                        onTitleChange = viewModel::onTitleChange,
                        onDescriptionChange = viewModel::onDescriptionChange,
                        onBenefitsChange = viewModel::onBenefitsChange
                    )
                    3 -> ReviewStep(state = state)
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (state.currentStep > 1) {
                    AguiaButton(
                        text = "Anterior",
                        onClick = viewModel::previousStep,
                        modifier = Modifier.weight(1f),
                        outline = true
                    )
                }
                
                AguiaButton(
                    text = if (state.currentStep == 3) "Enviar" else "Próximo",
                    onClick = {
                        if (state.currentStep == 3) viewModel.submitIdea()
                        else viewModel.nextStep()
                    },
                    modifier = Modifier.weight(1f),
                    enabled = when (state.currentStep) {
                        1 -> state.isStep1Valid
                        2 -> state.isStep2Valid
                        else -> !state.isLoading
                    },
                    isLoading = state.isLoading
                )
            }
        }
    }
}

@Composable
private fun StepIndicator(currentStep: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        StepItem(label = "Categoria", step = 1, currentStep = currentStep, modifier = Modifier.weight(1f))
        HorizontalDivider(modifier = Modifier.width(16.dp), color = AguiaColors.TextSecondary.copy(alpha = 0.2f))
        StepItem(label = "Detalhes", step = 2, currentStep = currentStep, modifier = Modifier.weight(1f))
        HorizontalDivider(modifier = Modifier.width(16.dp), color = AguiaColors.TextSecondary.copy(alpha = 0.2f))
        StepItem(label = "Revisão", step = 3, currentStep = currentStep, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun StepItem(label: String, step: Int, currentStep: Int, modifier: Modifier = Modifier) {
    val isActive = step <= currentStep
    val isCurrent = step == currentStep
    
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(if (isActive) AguiaColors.PrimaryBlue else AguiaColors.TextSecondary.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = step.toString(),
                style = MaterialTheme.typography.labelSmall,
                color = AguiaColors.CardWhite,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isCurrent) AguiaColors.PrimaryBlue else AguiaColors.TextSecondary,
            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun CategoryStep(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    val categories = listOf(
        CategoryItem("Atendimento", Icons.Default.Headset),
        CategoryItem("Segurança", Icons.Default.Security),
        CategoryItem("Operação", Icons.Default.DirectionsBus),
        CategoryItem("Manutenção", Icons.Default.Build),
        CategoryItem("Tecnologia", Icons.Default.Laptop),
        CategoryItem("Sustentabilidade", Icons.Default.Eco),
        CategoryItem("Pessoas", Icons.Default.Groups),
        CategoryItem("Outros", Icons.Default.MoreHoriz)
    )

    Column {
        Text(
            text = "Em qual categoria sua ideia se encaixa?",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(categories) { category ->
                val isSelected = selectedCategory == category.name
                Box(
                    modifier = Modifier
                        .height(100.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) AguiaColors.PrimaryBlue.copy(alpha = 0.1f) else AguiaColors.CardWhite)
                        .border(
                            width = 2.dp,
                            color = if (isSelected) AguiaColors.PrimaryBlue else Color.Transparent,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { onCategorySelected(category.name) }
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = category.icon,
                            contentDescription = null,
                            tint = if (isSelected) AguiaColors.PrimaryBlue else AguiaColors.TextSecondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = category.name,
                            style = MaterialTheme.typography.labelLarge,
                            color = if (isSelected) AguiaColors.PrimaryBlue else AguiaColors.TextPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailsStep(
    title: String,
    description: String,
    benefits: String,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onBenefitsChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "Conte-nos mais sobre sua ideia",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        AguiaTextField(
            value = title,
            onValueChange = onTitleChange,
            label = "Título da sugestão"
        )
        AguiaTextField(
            value = description,
            onValueChange = onDescriptionChange,
            label = "Descrição detalhada",
            singleLine = false,
            modifier = Modifier.heightIn(min = 120.dp)
        )
        AguiaTextField(
            value = benefits,
            onValueChange = onBenefitsChange,
            label = "Principais benefícios",
            singleLine = false,
            modifier = Modifier.heightIn(min = 100.dp)
        )
    }
}

@Composable
private fun ReviewStep(state: CreateIdeaState) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "Revise sua sugestão antes de enviar",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = AguiaColors.CardWhite),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ReviewItem(label = "Categoria", value = state.category)
                ReviewItem(label = "Título", value = state.title)
                ReviewItem(label = "Descrição", value = state.description)
                ReviewItem(label = "Benefícios", value = state.benefits)
            }
        }
        
        Text(
            text = "Ao enviar, sua ideia passará por uma análise técnica e você poderá acompanhar o status em 'Minhas Sugestões'.",
            style = MaterialTheme.typography.bodySmall,
            color = AguiaColors.TextSecondary
        )
    }
}

@Composable
private fun ReviewItem(label: String, value: String) {
    Column {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = AguiaColors.PrimaryBlue)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, color = AguiaColors.TextPrimary)
    }
}

private data class CategoryItem(val name: String, val icon: ImageVector)
