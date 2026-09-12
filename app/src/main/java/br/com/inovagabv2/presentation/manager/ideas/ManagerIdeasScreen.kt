package br.com.inovagabv2.presentation.manager.ideas

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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

    val filterTabs = listOf("Todas", "Enviadas", "Em análise", "Aprovadas", "Rejeitadas")
    var selectedFilterIndex by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            AguiaTopBar(
                role = Role.GESTOR,
                showRoleBadge = true
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
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header Title
                item {
                    Column {
                        Text(
                            text = "Ideias para análise",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = AguiaColors.NavyDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Avalie e direcione as sugestões enviadas pela equipe.",
                            fontSize = 14.sp,
                            color = AguiaColors.TextSecondary
                        )
                    }
                }

                // Search Bar
                item {
                    OutlinedTextField(
                        value = state.searchQuery,
                        onValueChange = viewModel::onSearchQueryChange,
                        placeholder = { Text("Buscar ideias...", color = AguiaColors.TextSecondary, fontSize = 14.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = AguiaColors.TextSecondary) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = AguiaColors.PrimaryBlue,
                            unfocusedBorderColor = Color(0xFFE2E8F0)
                        ),
                        singleLine = true
                    )
                }

                // Filter Pills Row
                item {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filterTabs.size) { index ->
                            val isSelected = selectedFilterIndex == index
                            Surface(
                                modifier = Modifier.clickable {
                                    selectedFilterIndex = index
                                    val targetStatus = when (index) {
                                        1 -> IdeaStatus.ENVIADA
                                        2 -> IdeaStatus.EM_ANALISE
                                        3 -> IdeaStatus.APROVADA
                                        4 -> IdeaStatus.REJEITADA
                                        else -> null
                                    }
                                    viewModel.onStatusFilterChange(targetStatus)
                                },
                                shape = RoundedCornerShape(20.dp),
                                color = if (isSelected) AguiaColors.PrimaryBlue else Color.White,
                                border = if (isSelected) null else BorderStroke(1.dp, Color(0xFF3B82F6))
                            ) {
                                Text(
                                    text = filterTabs[index],
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else AguiaColors.PrimaryBlue,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }

                // Loading / Empty / List State
                if (state.isLoading) {
                    item {
                        AguiaLoadingState(message = "Carregando ideias...")
                    }
                } else if (state.ideas.isEmpty()) {
                    item {
                        AguiaEmptyState(
                            message = "Nenhuma ideia encontrada para os filtros selecionados."
                        )
                    }
                } else {
                    items(state.ideas, key = { it.id }) { idea ->
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
