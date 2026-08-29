package br.com.inovagabv2.presentation.leadership

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.TrendingUp
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
fun LeadershipResultsScreen(
    viewModel: LeadershipResultsViewModel,
    onNavigateToDashboard: () -> Unit,
    onNavigateToProjects: () -> Unit,
    onNavigateToStrategy: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            AguiaTopBar(title = "Mensuração de Resultados")
        },
        bottomBar = {
            AguiaBottomBar(
                currentRoute = Screen.LeadershipResults.route,
                role = Role.LIDERANCA,
                onNavigate = { route ->
                    when (route) {
                        Screen.LeadershipDashboard.route -> onNavigateToDashboard()
                        Screen.LeadershipProjects.route -> onNavigateToProjects()
                        Screen.LeadershipResults.route -> { /* Already here */ }
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
            state.data?.let { data ->
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        AguiaDashboardCard(
                            title = "ROI Global",
                            value = data.roi,
                            icon = Icons.Default.TrendingUp,
                            color = AguiaColors.SuccessGreen,
                            modifier = Modifier.weight(1f)
                        )
                        AguiaDashboardCard(
                            title = "Lucro Total",
                            value = data.profit,
                            icon = Icons.Default.AttachMoney,
                            color = AguiaColors.PrimaryBlue,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Text(
                        text = "Resultados por Projeto",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AguiaColors.NavyDark
                    )
                }

                items(data.resultsByProject) { result ->
                    AguiaProjectResultCard(projectResult = result)
                }
            }
        }
    }
}
