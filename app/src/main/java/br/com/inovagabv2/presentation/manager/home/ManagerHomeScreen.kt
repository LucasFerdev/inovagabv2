package br.com.inovagabv2.presentation.manager.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.inovagabv2.core.designsystem.AguiaColors
import br.com.inovagabv2.core.designsystem.components.*
import br.com.inovagabv2.core.navigation.Screen
import br.com.inovagabv2.domain.model.Role

@Composable
fun ManagerHomeScreen(
    onNavigateToIdeas: () -> Unit,
    onNavigateToIdeaDetails: (String) -> Unit,
    onNavigateToProjects: () -> Unit,
    onNavigateToStrategy: () -> Unit,
    onNavigateToProfile: () -> Unit,
    viewModel: ManagerHomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            AguiaTopBar(
                backgroundColor = AguiaColors.ManagerPurple,
                contentColor = Color.White
            )
        },
        bottomBar = {
            AguiaBottomBar(
                currentRoute = Screen.ManagerHome.route,
                role = Role.GESTOR,
                onNavigate = { route ->
                    when (route) {
                        Screen.ManagerHome.route -> { /* Already here */ }
                        Screen.ManagerIdeas.route -> onNavigateToIdeas()
                        Screen.ManagerProjects.route -> onNavigateToProjects()
                        Screen.OperatorStrategy.route -> onNavigateToStrategy()
                        Screen.Profile.route -> onNavigateToProfile()
                    }
                }
            )
        },
        containerColor = AguiaColors.Background
    ) { padding ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AguiaColors.ManagerPurple)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Olá, ${state.user?.name ?: "Gestor"}!",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = AguiaColors.TextPrimary
                    )
                    Text(
                        text = "Confira o resumo da inovação hoje.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AguiaColors.TextSecondary
                    )
                }

                item {
                    Text(
                        text = "Resumo",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AguiaColors.TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AguiaMetricCard(
                            label = "Recebidas",
                            value = state.metrics.receivedIdeas.toString(),
                            color = AguiaColors.TextPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        AguiaMetricCard(
                            label = "Em análise",
                            value = state.metrics.inAnalysisIdeas.toString(),
                            color = AguiaColors.StatusInAnalysis,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AguiaMetricCard(
                            label = "Aprovadas",
                            value = state.metrics.approvedIdeas.toString(),
                            color = AguiaColors.SuccessGreen,
                            modifier = Modifier.weight(1f)
                        )
                        AguiaMetricCard(
                            label = "Projetos",
                            value = state.metrics.activeProjects.toString(),
                            color = AguiaColors.PrimaryBlue,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onNavigateToProjects() }
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Ideias para análise",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AguiaColors.TextPrimary
                        )
                        TextButton(onClick = onNavigateToIdeas) {
                            Text(
                                text = "Ver todas",
                                color = AguiaColors.ManagerPurple
                            )
                        }
                    }
                }

                if (state.ideas.isEmpty()) {
                    item {
                        Text(
                            text = "Nenhuma ideia pendente de análise.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = AguiaColors.TextSecondary,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    }
                } else {
                    items(state.ideas) { idea ->
                        AguiaIdeaCard(
                            idea = idea,
                            onClick = { onNavigateToIdeaDetails(idea.id) }
                        )
                    }
                }

                item {
                    AguiaButton(
                        text = "Analisar ideias",
                        onClick = onNavigateToIdeas,
                        modifier = Modifier.fillMaxWidth(),
                        containerColor = AguiaColors.ManagerPurple
                    )
                }
            }
        }
    }
}
