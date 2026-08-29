package br.com.inovagabv2.presentation.manager.ideas.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onBackClick()
        }
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
                            text = "Descrição",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = AguiaColors.TextPrimary
                        )
                        Text(
                            text = idea.description,
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
                                text = "Avaliação",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = AguiaColors.TextPrimary
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            
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
                                    text = "Aprovar",
                                    onClick = { viewModel.onDecision(IdeaStatus.APROVADA) },
                                    modifier = Modifier.weight(1f),
                                    containerColor = AguiaColors.SuccessGreen,
                                    isLoading = state.isSubmitting,
                                    enabled = state.selectedPriority != null
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                AguiaButton(
                                    text = "Pedir Ajustes",
                                    onClick = { viewModel.onDecision(IdeaStatus.AJUSTES_SOLICITADOS) },
                                    modifier = Modifier.weight(1f),
                                    outline = true,
                                    containerColor = AguiaColors.ManagerPurple,
                                    isLoading = state.isSubmitting
                                )
                                AguiaButton(
                                    text = "Rejeitar",
                                    onClick = { viewModel.onDecision(IdeaStatus.REJEITADA) },
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
}
