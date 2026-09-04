package br.com.inovagabv2.presentation.manager.ideas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val filterTabs = listOf("Todas", "Enviadas", "Em análise", "Aprovadas")
    var selectedFilterIndex by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            AguiaTopBar(roleTag = "GESTOR")
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
            // Search Bar & Filter Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = viewModel::onSearchQueryChange,
                    placeholder = { Text("Buscar ideias...", color = AguiaColors.TextSecondary, fontSize = 14.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = AguiaColors.TextSecondary) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = AguiaColors.CardWhite,
                        unfocusedContainerColor = AguiaColors.CardWhite,
                        focusedBorderColor = AguiaColors.PrimaryBlue.copy(alpha = 0.3f),
                        unfocusedBorderColor = Color.Transparent
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = { /* Filter action */ },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "Filtrar",
                        tint = AguiaColors.NavyDark
                    )
                }
            }

            // Filter Pills Row
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filterTabs.size) { index ->
                    val isSelected = selectedFilterIndex == index
                    Surface(
                        onClick = {
                            selectedFilterIndex = index
                            val targetStatus = when (index) {
                                1 -> IdeaStatus.ENVIADA
                                2 -> IdeaStatus.EM_ANALISE
                                3 -> IdeaStatus.APROVADA
                                else -> null
                            }
                            viewModel.onStatusFilterChange(targetStatus)
                        },
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) AguiaColors.PrimaryBlue else AguiaColors.CardWhite,
                        shadowElevation = if (isSelected) 0.dp else 1.dp
                    ) {
                        Text(
                            text = filterTabs[index],
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else AguiaColors.TextPrimary,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AguiaColors.PrimaryBlue)
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
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
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
