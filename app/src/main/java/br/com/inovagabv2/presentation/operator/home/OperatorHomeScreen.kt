package br.com.inovagabv2.presentation.operator.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.com.inovagabv2.core.designsystem.AguiaColors
import br.com.inovagabv2.core.designsystem.components.*

import br.com.inovagabv2.core.navigation.Screen
import br.com.inovagabv2.domain.model.Role

@Composable
fun OperatorHomeScreen(
    viewModel: OperatorHomeViewModel,
    onNavigateToCreateIdea: () -> Unit,
    onNavigateToStrategies: () -> Unit,
    onNavigateToMyIdeas: () -> Unit,
    onNavigateToIdeaDetails: (String) -> Unit,
    onNavigateToCommunications: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            AguiaTopBar()
        },
        bottomBar = {
            AguiaBottomBar(
                currentRoute = Screen.OperatorHome.route,
                role = Role.OPERADOR,
                onNavigate = { route ->
                    when (route) {
                        Screen.OperatorHome.route -> { /* Already here */ }
                        Screen.MyIdeas.route -> onNavigateToMyIdeas()
                        Screen.OperatorCommunications.route -> onNavigateToCommunications()
                        Screen.Profile.route -> onNavigateToProfile()
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToCreateIdea,
                containerColor = AguiaColors.PrimaryBlue,
                contentColor = AguiaColors.CardWhite,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Nova sugestão") }
            )
        },
        containerColor = AguiaColors.Background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Column {
                    Text(
                        text = "Olá, ${state.userName}!",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = AguiaColors.TextPrimary
                    )
                    Text(
                        text = "Como você vai inovar hoje?",
                        style = MaterialTheme.typography.bodyLarge,
                        color = AguiaColors.TextSecondary
                    )
                }
            }

            item {
                Column {
                    Text(
                        text = "Resumo",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AguiaColors.TextPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AguiaMetricCard(
                            label = "Enviadas",
                            value = state.sentCount.toString(),
                            color = AguiaColors.StatusSent,
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToMyIdeas
                        )
                        AguiaMetricCard(
                            label = "Em análise",
                            value = state.inAnalysisCount.toString(),
                            color = AguiaColors.StatusInAnalysis,
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToMyIdeas
                        )
                        AguiaMetricCard(
                            label = "Aprovadas",
                            value = state.approvedCount.toString(),
                            color = AguiaColors.StatusApproved,
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToMyIdeas
                        )
                    }
                }
            }

            if (state.strategies.isNotEmpty()) {
                item {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Orientações Estratégicas",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = AguiaColors.TextPrimary
                            )
                            TextButton(onClick = onNavigateToStrategies) {
                                Text("Ver todas")
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        AguiaStrategyCard(
                            strategy = state.strategies.first(),
                            onClick = onNavigateToStrategies
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Acompanhe suas sugestões",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AguiaColors.TextPrimary
                    )
                    TextButton(onClick = onNavigateToMyIdeas) {
                        Text("Ver todas")
                    }
                }
            }

            items(state.recentIdeas) { idea ->
                AguiaIdeaCard(
                    idea = idea,
                    onClick = { onNavigateToIdeaDetails(idea.id) }
                )
            }

            if (state.recentIdeas.isEmpty()) {
                item {
                    Text(
                        text = "Você ainda não enviou sugestões.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AguiaColors.TextSecondary,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(80.dp)) // Space for FAB
            }
        }
    }
}
