package br.com.inovagabv2.presentation.leadership

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.inovagabv2.core.designsystem.AguiaColors
import br.com.inovagabv2.core.designsystem.components.*
import br.com.inovagabv2.core.navigation.Screen
import br.com.inovagabv2.domain.model.Role

@Composable
fun LeadershipProjectsScreen(
    viewModel: LeadershipProjectsViewModel,
    onNavigateToDashboard: () -> Unit,
    onNavigateToResults: () -> Unit,
    onNavigateToStrategy: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            AguiaTopBar(title = "Gestão de Portfólio")
        },
        bottomBar = {
            AguiaBottomBar(
                currentRoute = Screen.LeadershipProjects.route,
                role = Role.LIDERANCA,
                onNavigate = { route ->
                    when (route) {
                        Screen.LeadershipDashboard.route -> onNavigateToDashboard()
                        Screen.LeadershipProjects.route -> { /* Already here */ }
                        Screen.LeadershipResults.route -> onNavigateToResults()
                        Screen.LeadershipStrategy.route -> onNavigateToStrategy()
                        Screen.Profile.route -> onNavigateToProfile()
                    }
                }
            )
        },
        containerColor = AguiaColors.Background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AguiaMetricCard(
                        label = "Investimento Total",
                        value = "R$ ${String.format("%.2f", state.totalInvestment)}",
                        color = AguiaColors.PrimaryBlue,
                        modifier = Modifier.weight(1f)
                    )
                    AguiaMetricCard(
                        label = "Total Projetos",
                        value = state.projects.size.toString(),
                        color = AguiaColors.NavyDark,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            items(state.projects) { project ->
                AguiaProjectCard(
                    project = project,
                    onClick = { /* Navigate to details if needed */ }
                )
            }
        }
    }
}
