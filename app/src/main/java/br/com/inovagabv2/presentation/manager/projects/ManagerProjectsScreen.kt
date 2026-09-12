package br.com.inovagabv2.presentation.manager.projects

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
                role = Role.GESTOR,
                showRoleBadge = true
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
            ExtendedFloatingActionButton(
                onClick = onNavigateToCreateProject,
                containerColor = AguiaColors.PrimaryBlue,
                contentColor = Color.White,
                shape = RoundedCornerShape(24.dp),
                elevation = FloatingActionButtonDefaults.elevation(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Novo projeto",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
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
                            text = "Projetos",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = AguiaColors.NavyDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Acompanhe o portfólio de projetos de inovação.",
                            fontSize = 14.sp,
                            color = AguiaColors.TextSecondary
                        )
                    }
                }

                // Tab Row: Meus projetos | Todos
                item {
                    TabRow(
                        selectedTabIndex = state.selectedTab,
                        containerColor = Color.White,
                        contentColor = AguiaColors.PrimaryBlue,
                        indicator = { tabPositions ->
                            if (state.selectedTab < tabPositions.size) {
                                TabRowDefaults.SecondaryIndicator(
                                    Modifier.tabIndicatorOffset(tabPositions[state.selectedTab]),
                                    color = AguiaColors.PrimaryBlue
                                )
                            }
                        }
                    ) {
                        Tab(
                            selected = state.selectedTab == 0,
                            onClick = { viewModel.onTabSelected(0) },
                            text = {
                                Text(
                                    text = "Meus projetos",
                                    fontWeight = if (state.selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                                    color = if (state.selectedTab == 0) AguiaColors.PrimaryBlue else AguiaColors.TextSecondary
                                )
                            }
                        )
                        Tab(
                            selected = state.selectedTab == 1,
                            onClick = { viewModel.onTabSelected(1) },
                            text = {
                                Text(
                                    text = "Todos",
                                    fontWeight = if (state.selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                                    color = if (state.selectedTab == 1) AguiaColors.PrimaryBlue else AguiaColors.TextSecondary
                                )
                            }
                        )
                    }
                }

                // Loading / Empty / List State
                if (state.isLoading) {
                    item {
                        AguiaLoadingState(message = "Carregando projetos...")
                    }
                } else if (state.projects.isEmpty()) {
                    item {
                        AguiaEmptyState(
                            message = "Nenhum projeto encontrado."
                        )
                    }
                } else {
                    items(state.projects, key = { it.id }) { project ->
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
