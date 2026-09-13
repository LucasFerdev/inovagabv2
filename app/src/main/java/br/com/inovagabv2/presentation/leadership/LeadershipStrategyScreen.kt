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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Description
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
import br.com.inovagabv2.domain.model.Role
import br.com.inovagabv2.domain.model.Strategy

@Composable
fun LeadershipStrategyScreen(
    viewModel: LeadershipStrategyViewModel = hiltViewModel(),
    onNavigateToCreate: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    onNavigateToDashboard: () -> Unit,
    onNavigateToProjects: () -> Unit,
    onNavigateToIdeas: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToPanel: (String) -> Unit
) {
    val user by viewModel.user.collectAsState()
    val strategies by viewModel.filteredStrategies.collectAsState()
    val activeCount by viewModel.activeCount.collectAsState()
    val draftCount by viewModel.draftCount.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val filterTabs = listOf("Todas", "Ativas", "Rascunhos")
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
                currentRoute = Screen.LeadershipDashboard.route,
                role = currentRole,
                onNavigate = { route ->
                    when (route) {
                        Screen.LeadershipDashboard.route -> onNavigateToDashboard()
                        Screen.LeadershipProjects.route -> onNavigateToProjects()
                        Screen.LeadershipStrategy.route -> { /* Already here */ }
                        Screen.Profile.route -> onNavigateToProfile()
                        else -> onNavigateToIdeas()
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToCreate,
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
                    text = "Nova",
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
                            text = "Estratégias",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = AguiaColors.NavyDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Direcione a inovação para os objetivos do Grupo.",
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
                        placeholder = { Text("Buscar estratégias", color = AguiaColors.TextSecondary, fontSize = 14.sp) },
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
                                    val targetFilter = when (index) {
                                        1 -> "ATIVA"
                                        2 -> "RASCUNHO"
                                        else -> null
                                    }
                                    viewModel.onStatusFilterSelected(targetFilter)
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
                        LeadershipStrategySummaryChip(
                            count = activeCount.toString(),
                            label = "Ativas",
                            icon = Icons.Outlined.Check,
                            iconColor = Color(0xFF16A34A),
                            modifier = Modifier.weight(1f)
                        )
                        LeadershipStrategySummaryChip(
                            count = draftCount.toString(),
                            label = "Rascunho",
                            icon = Icons.Outlined.Description,
                            iconColor = Color(0xFF0284C7),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Section Title: Estratégias do Grupo
                item {
                    Text(
                        text = "Estratégias do Grupo",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AguiaColors.NavyDark
                    )
                }

                // Loading / Empty / List State
                if (isLoading) {
                    item {
                        AguiaLoadingState(message = "Carregando estratégias...")
                    }
                } else if (strategies.isEmpty()) {
                    item {
                        AguiaEmptyState(
                            message = "Nenhuma estratégia cadastrada."
                        )
                    }
                } else {
                    items(strategies, key = { it.id }) { strategy ->
                        LeadershipStrategyCardItem(
                            strategy = strategy,
                            onOpenPanel = { onNavigateToPanel(strategy.id) },
                            onEdit = { onNavigateToEdit(strategy.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LeadershipStrategySummaryChip(
    count: String,
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
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column {
                Text(
                    text = count,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = AguiaColors.NavyDark
                )
                Text(
                    text = label,
                    fontSize = 11.sp,
                    color = AguiaColors.TextSecondary
                )
            }
        }
    }
}

@Composable
private fun LeadershipStrategyCardItem(
    strategy: Strategy,
    onOpenPanel: () -> Unit,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { if (strategy.isPublished) onOpenPanel() else onEdit() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFEEF2F6))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = strategy.title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = AguiaColors.NavyDark,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                AguiaStatusChip(statusText = if (strategy.isPublished) "ATIVA" else "RASCUNHO")
            }

            Spacer(modifier = Modifier.height(4.dp))

            strategy.category?.let { cat ->
                Text(
                    text = "Categoria: $cat",
                    fontSize = 12.sp,
                    color = AguiaColors.TextSecondary
                )
            }

            strategy.campaign?.let { camp ->
                Text(
                    text = "Campanha: $camp",
                    fontSize = 12.sp,
                    color = AguiaColors.TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = strategy.publishedDate ?: strategy.createdAt,
                    fontSize = 12.sp,
                    color = AguiaColors.TextSecondary
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { if (strategy.isPublished) onOpenPanel() else onEdit() }
                ) {
                    Text(
                        text = if (strategy.isPublished) "Ver painel" else "Continuar edição",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = AguiaColors.PrimaryBlue
                    )
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = AguiaColors.PrimaryBlue,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
