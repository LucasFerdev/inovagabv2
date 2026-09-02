package br.com.inovagabv2.presentation.manager.projects

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.inovagabv2.core.designsystem.AguiaColors
import br.com.inovagabv2.core.designsystem.components.*
import br.com.inovagabv2.core.navigation.Screen
import br.com.inovagabv2.domain.model.Role

@Composable
fun ManagerProjectsScreen(
    onNavigateToDetails: (String) -> Unit,
    onNavigateToCreateProject: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToIdeas: () -> Unit,
    onNavigateToStrategy: () -> Unit,
    onNavigateToProfile: () -> Unit,
    viewModel: ManagerProjectsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            AguiaTopBar(
                title = "Projetos",
                backgroundColor = AguiaColors.ManagerPurple,
                contentColor = AguiaColors.CardWhite
            )
        },
        bottomBar = {
            AguiaBottomBar(
                currentRoute = Screen.ManagerProjects.route,
                role = Role.GESTOR,
                onNavigate = { route ->
                    when (route) {
                        Screen.ManagerHome.route -> onNavigateToHome()
                        Screen.ManagerIdeas.route -> onNavigateToIdeas()
                        Screen.ManagerProjects.route -> { /* Already here */ }
                        Screen.OperatorStrategy.route -> onNavigateToStrategy()
                        Screen.Profile.route -> onNavigateToProfile()
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreateProject,
                containerColor = AguiaColors.ManagerPurple,
                contentColor = AguiaColors.CardWhite
            ) {
                Icon(Icons.Default.Add, contentDescription = "Novo Projeto")
            }
        },
        containerColor = AguiaColors.Background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            TabRow(
                selectedTabIndex = state.selectedTab,
                containerColor = AguiaColors.CardWhite,
                contentColor = AguiaColors.ManagerPurple,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[state.selectedTab]),
                        color = AguiaColors.ManagerPurple
                    )
                }
            ) {
                Tab(
                    selected = state.selectedTab == 0,
                    onClick = { viewModel.onTabSelected(0) },
                    text = { Text("Meus projetos") }
                )
                Tab(
                    selected = state.selectedTab == 1,
                    onClick = { viewModel.onTabSelected(1) },
                    text = { Text("Todos") }
                )
            }

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AguiaColors.ManagerPurple)
                }
            } else if (state.projects.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Nenhum projeto encontrado.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AguiaColors.TextSecondary
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.projects) { project ->
                        AguiaProjectCard(
                            project = project,
                            onClick = { onNavigateToDetails(project.id) }
                        )
                    }
                }
            }
        }
    }
}
