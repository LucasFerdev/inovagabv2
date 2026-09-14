package br.com.inovagabv2.presentation.operator.create

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.sp
import br.com.inovagabv2.core.designsystem.AguiaColors
import br.com.inovagabv2.core.designsystem.components.AguiaButton
import br.com.inovagabv2.core.designsystem.components.AguiaTextField
import br.com.inovagabv2.core.designsystem.components.AguiaTopBar
import br.com.inovagabv2.domain.model.Strategy

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
                roleTag = "OPERADOR",
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
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            StepIndicator(currentStep = state.currentStep)
            
            Spacer(modifier = Modifier.height(20.dp))

            Box(modifier = Modifier.weight(1f)) {
                when (state.currentStep) {
                    1 -> CategoryStep(
                        state = state,
                        onStrategySelected = viewModel::onStrategySelected,
                        selectedCategory = state.category,
                        onCategorySelected = viewModel::onCategoryChange
                    )
                    2 -> DetailsStep(
                        title = state.title,
                        problem = state.problem,
                        solution = state.proposedSolution,
                        benefits = state.expectedBenefits,
                        onTitleChange = viewModel::onTitleChange,
                        onProblemChange = viewModel::onProblemChange,
                        onSolutionChange = viewModel::onProposedSolutionChange,
                        onBenefitsChange = viewModel::onExpectedBenefitsChange
                    )
                    3 -> ReviewStep(state = state)
                }
            }

            if (!state.error.isNullOrBlank()) {
                Text(
                    text = state.error!!,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (state.currentStep > 1) {
                    AguiaButton(
                        text = "Anterior",
                        onClick = viewModel::previousStep,
                        modifier = Modifier.weight(1f),
                        outline = true
                    )
                }
                
                Button(
                    onClick = {
                        if (state.currentStep == 3) viewModel.submitIdea()
                        else viewModel.nextStep()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    enabled = when (state.currentStep) {
                        1 -> state.isStep1Valid
                        2 -> state.isStep2Valid
                        else -> !state.isLoading && state.selectedStrategy != null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AguiaColors.PrimaryBlue,
                        contentColor = Color.White
                    )
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = if (state.currentStep == 3) "Enviar" else "Próximo",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
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
        StepItem(label = "Estratégia & Categoria", step = 1, currentStep = currentStep, modifier = Modifier.weight(1f))
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
                .size(28.dp)
                .clip(CircleShape)
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
            fontSize = 10.sp,
            color = if (isCurrent) AguiaColors.PrimaryBlue else AguiaColors.TextSecondary,
            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun CategoryStep(
    state: CreateIdeaState,
    onStrategySelected: (Strategy) -> Unit,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    val categories = listOf(
        CategoryItem("Atendimento", Icons.Default.Person),
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
            text = "Estratégia Vinculada",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = AguiaColors.NavyDark
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (state.isLoadingStrategies) {
            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = AguiaColors.PrimaryBlue)
        } else if (state.activeStrategies.isEmpty()) {
            Text(
                text = "Nenhuma estratégia ativa disponível. Aguarde a definição de uma estratégia pela Liderança.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Medium
            )
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.activeStrategies) { strategy ->
                    val isSelected = state.selectedStrategy?.id == strategy.id
                    Surface(
                        modifier = Modifier.clickable { onStrategySelected(strategy) },
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) AguiaColors.PrimaryBlue else Color.White,
                        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3B82F6))
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

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "Selecione a categoria da sua sugestão",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = AguiaColors.NavyDark
        )
        Spacer(modifier = Modifier.height(12.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(categories) { category ->
                val isSelected = selectedCategory == category.name
                Card(
                    modifier = Modifier
                        .height(56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(
                            width = if (isSelected) 2.dp else 0.dp,
                            color = if (isSelected) AguiaColors.PrimaryBlue else Color.Transparent,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { onCategorySelected(category.name) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) AguiaColors.PrimaryBlue.copy(alpha = 0.08f) else AguiaColors.CardWhite
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) AguiaColors.PrimaryBlue else AguiaColors.PrimaryBlue.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = category.icon,
                                contentDescription = null,
                                tint = if (isSelected) Color.White else AguiaColors.PrimaryBlue,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = category.name,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) AguiaColors.PrimaryBlue else AguiaColors.NavyDark
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
    problem: String,
    solution: String,
    benefits: String,
    onTitleChange: (String) -> Unit,
    onProblemChange: (String) -> Unit,
    onSolutionChange: (String) -> Unit,
    onBenefitsChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Conte-nos mais sobre sua ideia",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = AguiaColors.NavyDark
        )
        AguiaTextField(
            value = title,
            onValueChange = onTitleChange,
            label = "Título da sugestão"
        )
        AguiaTextField(
            value = problem,
            onValueChange = onProblemChange,
            label = "Problema identificado",
            singleLine = false,
            modifier = Modifier.heightIn(min = 70.dp)
        )
        AguiaTextField(
            value = solution,
            onValueChange = onSolutionChange,
            label = "Solução proposta",
            singleLine = false,
            modifier = Modifier.heightIn(min = 70.dp)
        )
        AguiaTextField(
            value = benefits,
            onValueChange = onBenefitsChange,
            label = "Benefícios esperados",
            singleLine = false,
            modifier = Modifier.heightIn(min = 70.dp)
        )
    }
}

@Composable
private fun ReviewStep(state: CreateIdeaState) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "Revise sua sugestão antes de enviar",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = AguiaColors.NavyDark
        )
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = AguiaColors.CardWhite),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ReviewItem(label = "Estratégia", value = state.selectedStrategy?.title ?: "Nenhuma")
                ReviewItem(label = "Categoria", value = state.category)
                ReviewItem(label = "Título", value = state.title)
                ReviewItem(label = "Problema", value = state.problem.ifBlank { state.proposedSolution })
                ReviewItem(label = "Solução Proposta", value = state.proposedSolution.ifBlank { state.problem })
                ReviewItem(label = "Benefícios", value = state.expectedBenefits)
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
