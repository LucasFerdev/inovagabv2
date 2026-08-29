package br.com.inovagabv2.presentation.operator.myideas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.inovagabv2.core.designsystem.AguiaColors
import br.com.inovagabv2.core.designsystem.components.*
import br.com.inovagabv2.core.navigation.Screen
import br.com.inovagabv2.domain.model.IdeaStatus
import br.com.inovagabv2.domain.model.Role

@Composable
fun MyIdeasScreen(
    viewModel: MyIdeasViewModel,
    onBackClick: () -> Unit,
    onIdeaClick: (String) -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToCommunications: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val ideas by viewModel.filteredIdeas.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()

    Scaffold(
        topBar = {
            AguiaTopBar(
                title = "Minhas Sugestões",
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            AguiaBottomBar(
                currentRoute = Screen.MyIdeas.route,
                role = Role.OPERADOR,
                onNavigate = { route ->
                    when (route) {
                        Screen.OperatorHome.route -> onNavigateToHome()
                        Screen.MyIdeas.route -> { /* Already here */ }
                        Screen.OperatorCommunications.route -> onNavigateToCommunications()
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
            FilterRow(
                selectedFilter = selectedFilter,
                onFilterSelected = viewModel::onFilterSelected
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(ideas) { idea ->
                    AguiaIdeaCard(
                        idea = idea,
                        onClick = { onIdeaClick(idea.id) }
                    )
                }

                if (ideas.isEmpty()) {
                    item {
                        Text(
                            text = "Nenhuma sugestão encontrada.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = AguiaColors.TextSecondary,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterRow(
    selectedFilter: IdeaStatus?,
    onFilterSelected: (IdeaStatus?) -> Unit
) {
    val filters = listOf(null) + IdeaStatus.values().toList()

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(filters) { status ->
            FilterChip(
                selected = selectedFilter == status,
                onClick = { onFilterSelected(status) },
                label = {
                    Text(status?.displayName ?: "Todas")
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AguiaColors.PrimaryBlue.copy(alpha = 0.1f),
                    selectedLabelColor = AguiaColors.PrimaryBlue,
                    selectedLeadingIconColor = AguiaColors.PrimaryBlue
                )
            )
        }
    }
}
