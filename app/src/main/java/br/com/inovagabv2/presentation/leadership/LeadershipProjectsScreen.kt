package br.com.inovagabv2.presentation.leadership

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Work
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.inovagabv2.core.designsystem.AguiaColors
import br.com.inovagabv2.core.designsystem.components.*
import br.com.inovagabv2.core.navigation.Screen
import br.com.inovagabv2.domain.model.ProjectStatus
import br.com.inovagabv2.domain.model.Role
import br.com.inovagabv2.presentation.components.OnResumeEffect

@Composable
fun LeadershipProjectsScreen(
    onNavigateToDashboard: () -> Unit,
    onNavigateToIdeas: () -> Unit,
    onNavigateToStrategy: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToDetails: (String) -> Unit,
    viewModel: LeadershipProjectsViewModel = hiltViewModel()
) {
    val user by viewModel.user.collectAsState()
    val projects by viewModel.projects.collectAsState()
    val totalCount by viewModel.totalProjectsCount.collectAsState()
    val delayedCount by viewModel.delayedCount.collectAsState()
    val averageProgress by viewModel.averageProgress.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    OnResumeEffect(onResume = viewModel::refresh)

    val filterTabs = listOf("Todos", "Em andamento", "Planejados", "Concluídos")
    var selectedFilterIndex by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }

    val currentRole = user?.role ?: Role.LIDERANCA

    Scaffold(
        topBar = {
            AguiaTopBar(
                userName = user?.name,
                role = currentRole,
                showRoleBadge = true
            )
        },
        bottomBar = {
            AguiaBottomBar(
                currentRoute = Screen.LeadershipProjects.route,
                role = currentRole,
                onNavigate = { route ->
                    when (route) {
                        Screen.LeadershipDashboard.route -> onNavigateToDashboard()
                        Screen.LeadershipProjects.route -> { /* Already here */ }
                        Screen.LeadershipStrategy.route -> onNavigateToStrategy()
                        Screen.Profile.route -> onNavigateToProfile()
                        else -> onNavigateToIdeas()
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
                            text = "Projetos",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = AguiaColors.NavyDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Acompanhe execução, custos e resultados.",
                            fontSize = 14.sp,
                            color = AguiaColors.TextSecondary
                        )
                    }
                }

                // Search Bar
                item {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = {
                            searchQuery = it
                            viewModel.onSearchQueryChange(it)
                        },
                        placeholder = { Text("Buscar projetos", color = AguiaColors.TextSecondary, fontSize = 14.sp) },
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
                                        1 -> ProjectStatus.EM_ANDAMENTO
                                        2 -> ProjectStatus.PLANEJADO
                                        3 -> ProjectStatus.CONCLUIDO
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

                // Summary Row Cards
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        LeadershipProjectSummaryChip(
                            value = totalCount.toString(),
                            label = "Projetos",
                            icon = Icons.Outlined.Work,
                            iconColor = AguiaColors.PrimaryBlue,
                            modifier = Modifier.weight(1f)
                        )
                        LeadershipProjectSummaryChip(
                            value = delayedCount.toString(),
                            label = "Atrasados",
                            icon = Icons.Outlined.Schedule,
                            iconColor = Color(0xFFD97706),
                            modifier = Modifier.weight(1f)
                        )
                        LeadershipProjectSummaryChip(
                            value = "$averageProgress%",
                            label = "Progresso médio",
                            icon = Icons.Outlined.BarChart,
                            iconColor = Color(0xFF0284C7),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Section Title: Portfólio
                item {
                    Text(
                        text = "Portfólio",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AguiaColors.NavyDark
                    )
                }

                // Loading / Empty / List State
                if (isLoading) {
                    item {
                        AguiaLoadingState(message = "Carregando projetos...")
                    }
                } else if (error != null) {
                    item {
                        AguiaErrorState(
                            message = error ?: "Algo deu errado. Tente novamente.",
                            onRetry = viewModel::loadProjects
                        )
                    }
                } else if (projects.isEmpty()) {
                    item {
                        AguiaEmptyState(
                            message = "Nenhum projeto encontrado."
                        )
                    }
                } else {
                    items(projects, key = { it.id }) { project ->
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

@Composable
private fun LeadershipProjectSummaryChip(
    value: String,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFEEF2F6))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = value,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = AguiaColors.NavyDark
                )
                Text(
                    text = label,
                    fontSize = 10.sp,
                    color = AguiaColors.TextSecondary,
                    lineHeight = 12.sp
                )
            }
        }
    }
}
