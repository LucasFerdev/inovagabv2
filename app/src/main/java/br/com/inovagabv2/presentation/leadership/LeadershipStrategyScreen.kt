package br.com.inovagabv2.presentation.leadership

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import br.com.inovagabv2.core.designsystem.AguiaColors
import br.com.inovagabv2.core.designsystem.components.*
import br.com.inovagabv2.core.navigation.Screen
import br.com.inovagabv2.domain.model.Role

@Composable
fun LeadershipStrategyScreen(
    viewModel: LeadershipStrategyViewModel,
    onNavigateToCreate: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    onNavigateToDashboard: () -> Unit,
    onNavigateToProjects: () -> Unit,
    onNavigateToResults: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Publicadas", "Rascunhos")

    Scaffold(
        topBar = {
            AguiaTopBar(title = "Diretrizes Estratégicas")
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreate,
                containerColor = AguiaColors.PrimaryBlue,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nova Diretriz")
            }
        },
        bottomBar = {
            AguiaBottomBar(
                currentRoute = Screen.LeadershipStrategy.route,
                role = Role.LIDERANCA,
                onNavigate = { route ->
                    when (route) {
                        Screen.LeadershipDashboard.route -> onNavigateToDashboard()
                        Screen.LeadershipProjects.route -> onNavigateToProjects()
                        Screen.LeadershipResults.route -> onNavigateToResults()
                        Screen.LeadershipStrategy.route -> { /* Already here */ }
                        Screen.Profile.route -> onNavigateToProfile()
                    }
                }
            )
        },
        containerColor = AguiaColors.Background
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = AguiaColors.PrimaryBlue
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AguiaColors.PrimaryBlue)
                }
            } else {
                val filteredStrategies = state.strategies.filter { 
                    if (selectedTab == 0) it.isPublished else !it.isPublished 
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredStrategies) { strategy ->
                        AguiaStrategyCard(
                            strategy = strategy,
                            onClick = { onNavigateToEdit(strategy.id) },
                            onDelete = { viewModel.deleteStrategy(strategy.id) },
                            onTogglePublish = { viewModel.togglePublish(strategy) }
                        )
                    }
                }
            }
        }
    }
}
