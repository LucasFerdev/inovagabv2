package br.com.inovagabv2.presentation.manager.ideas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
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
import br.com.inovagabv2.domain.model.IdeaStatus
import br.com.inovagabv2.domain.model.Role

@Composable
fun ManagerIdeasScreen(
    onNavigateToDetails: (String) -> Unit,
    onBackClick: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToProjects: () -> Unit,
    onNavigateToStrategy: () -> Unit,
    onNavigateToProfile: () -> Unit,
    viewModel: ManagerIdeasViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            AguiaTopBar(
                title = "Analisar Ideias",
                onBackClick = onBackClick,
                backgroundColor = AguiaColors.ManagerPurple,
                contentColor = AguiaColors.CardWhite
            )
        },
        bottomBar = {
            AguiaBottomBar(
                currentRoute = Screen.ManagerIdeas.route,
                role = Role.GESTOR,
                onNavigate = { route ->
                    when (route) {
                        Screen.ManagerHome.route -> onNavigateToHome()
                        Screen.ManagerIdeas.route -> { /* Already here */ }
                        Screen.ManagerProjects.route -> onNavigateToProjects()
                        Screen.OperatorStrategy.route -> onNavigateToStrategy()
                        Screen.Profile.route -> onNavigateToProfile()
                    }
                }
            )
        },
        containerColor = AguiaColors.Background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            AguiaTextField(
                value = state.searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                label = "Buscar ideias...",
                modifier = Modifier.padding(16.dp)
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(IdeaStatus.entries) { status ->
                    FilterChip(
                        selected = state.selectedStatus == status,
                        onClick = { viewModel.onStatusFilterChange(status) },
                        label = { Text(status.displayName) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AguiaColors.ManagerPurple,
                            selectedLabelColor = AguiaColors.CardWhite
                        )
                    )
                }
            }

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AguiaColors.ManagerPurple)
                }
            } else if (state.ideas.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Nenhuma ideia encontrada.",
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
                    items(state.ideas) { idea ->
                        AguiaIdeaCard(
                            idea = idea,
                            onClick = { onNavigateToDetails(idea.id) }
                        )
                    }
                }
            }
        }
    }
}
