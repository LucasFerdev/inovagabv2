package br.com.inovagabv2.presentation.leadership

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.com.inovagabv2.core.designsystem.AguiaColors
import br.com.inovagabv2.core.designsystem.components.*
import br.com.inovagabv2.core.navigation.Screen
import br.com.inovagabv2.domain.model.Role

@Composable
fun LeadershipDashboardScreen(
    viewModel: LeadershipDashboardViewModel,
    onNavigateToProjects: () -> Unit,
    onNavigateToResults: () -> Unit,
    onNavigateToStrategy: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            AguiaTopBar()
        },
        bottomBar = {
            AguiaBottomBar(
                currentRoute = Screen.LeadershipDashboard.route,
                role = Role.LIDERANCA,
                onNavigate = { route ->
                    when (route) {
                        Screen.LeadershipDashboard.route -> { /* Already here */ }
                        Screen.LeadershipProjects.route -> onNavigateToProjects()
                        Screen.LeadershipResults.route -> onNavigateToResults()
                        Screen.LeadershipStrategy.route -> onNavigateToStrategy()
                        Screen.Profile.route -> onNavigateToProfile()
                    }
                }
            )
        },
        containerColor = AguiaColors.Background
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AguiaColors.PrimaryBlue)
            }
        } else if (state.error != null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text(text = state.error ?: "Erro ao carregar dados", color = AguiaColors.ErrorRed)
            }
        } else {
            state.data?.let { data ->
                DashboardContent(
                    data = data,
                    modifier = Modifier.padding(padding),
                    onNavigateToProjects = onNavigateToProjects,
                    onNavigateToStrategy = onNavigateToStrategy
                )
            }
        }
    }
}

@Composable
private fun DashboardContent(
    data: br.com.inovagabv2.domain.model.DashboardData,
    modifier: Modifier = Modifier,
    onNavigateToProjects: () -> Unit,
    onNavigateToStrategy: () -> Unit
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            Text(
                text = "Visão Geral de Impacto",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AguiaColors.NavyDark
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AguiaDashboardCard(
                        title = "ROI Global",
                        value = data.roi,
                        icon = Icons.AutoMirrored.Filled.TrendingUp,
                        color = AguiaColors.SuccessGreen,
                        trend = "+12% vs meta",
                        modifier = Modifier.weight(1f)
                    )
                    AguiaDashboardCard(
                        title = "Lucro Gerado",
                        value = data.profit,
                        icon = Icons.Default.AttachMoney,
                        color = AguiaColors.PrimaryBlue,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AguiaDashboardCard(
                        title = "Redução de Custos",
                        value = data.costReduction,
                        icon = Icons.Default.Analytics,
                        color = AguiaColors.ManagerPurple,
                        modifier = Modifier.weight(1f)
                    )
                    AguiaDashboardCard(
                        title = "Produtividade",
                        value = data.productivity,
                        icon = Icons.Default.Speed,
                        color = AguiaColors.WarningYellow,
                        trend = "+5% este mês",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        item {
            Text(
                text = "Resumo Operacional",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AguiaColors.NavyDark,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                AguiaMetricCard(
                    label = "Projetos em Andamento",
                    value = data.activeProjectsCount.toString(),
                    color = AguiaColors.PrimaryBlue,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToProjects
                )
                AguiaMetricCard(
                    label = "Ideias Aprovadas (Mês)",
                    value = data.approvedIdeasCount.toString(),
                    color = AguiaColors.SuccessGreen,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToStrategy
                )
            }
        }
    }
}

