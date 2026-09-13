package br.com.inovagabv2.presentation.leadership

import androidx.compose.foundation.BorderStroke
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
import br.com.inovagabv2.domain.model.Idea
import br.com.inovagabv2.domain.model.IdeaStatus
import br.com.inovagabv2.domain.model.Role

@Composable
fun LeadershipIdeasScreen(
    onNavigateToDetails: (String) -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToProjects: () -> Unit,
    onNavigateToStrategy: () -> Unit,
    onNavigateToProfile: () -> Unit,
    viewModel: LeadershipIdeasViewModel = hiltViewModel()
) {
    val user by viewModel.user.collectAsState()
    val ideas by viewModel.filteredIdeas.collectAsState()
    val inAnalysisCount by viewModel.inAnalysisCount.collectAsState()
    val approvedCount by viewModel.approvedCount.collectAsState()
    val rejectedCount by viewModel.rejectedCount.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val filterTabs = listOf("Todas", "Em análise", "Aprovadas", "Rejeitadas")
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
                        Screen.LeadershipDashboard.route -> onNavigateToHome()
                        Screen.LeadershipProjects.route -> onNavigateToProjects()
                        Screen.LeadershipStrategy.route -> onNavigateToStrategy()
                        Screen.Profile.route -> onNavigateToProfile()
                        else -> { /* Already here */ }
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
                            text = "Ideias",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = AguiaColors.NavyDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Acompanhe propostas e diretrizes atreladas.",
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
                                        1 -> IdeaStatus.EM_ANALISE
                                        2 -> IdeaStatus.APROVADA
                                        3 -> IdeaStatus.REJEITADA
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
                        LeadershipSummaryChip(
                            count = inAnalysisCount.toString(),
                            label = "Em análise",
                            bgColor = Color(0xFFFEF3C7),
                            textColor = Color(0xFFD97706),
                            modifier = Modifier.weight(1f)
                        )
                        LeadershipSummaryChip(
                            count = approvedCount.toString(),
                            label = "Aprovadas",
                            bgColor = Color(0xFFDCFCE7),
                            textColor = Color(0xFF16A34A),
                            modifier = Modifier.weight(1f)
                        )
                        LeadershipSummaryChip(
                            count = rejectedCount.toString(),
                            label = "Rejeitadas",
                            bgColor = Color(0xFFFEE2E2),
                            textColor = Color(0xFFDC2626),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Section Title: Para acompanhamento
                item {
                    Text(
                        text = "Para acompanhamento",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AguiaColors.NavyDark
                    )
                }

                // Loading / Empty / List State
                if (isLoading) {
                    item {
                        AguiaLoadingState(message = "Carregando ideias...")
                    }
                } else if (ideas.isEmpty()) {
                    item {
                        AguiaEmptyState(
                            message = "Nenhuma ideia encontrada."
                        )
                    }
                } else {
                    items(ideas, key = { it.id }) { idea ->
                        LeadershipIdeaConsultCard(
                            idea = idea,
                            onClick = { onNavigateToDetails(idea.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LeadershipSummaryChip(
    count: String,
    label: String,
    bgColor: Color,
    textColor: Color,
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
            Surface(
                shape = CircleShape,
                color = bgColor,
                modifier = Modifier.size(28.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = count,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = AguiaColors.TextSecondary
            )
        }
    }
}

@Composable
private fun LeadershipIdeaConsultCard(
    idea: Idea,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
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
                    text = idea.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = AguiaColors.NavyDark,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                AguiaStatusChip(status = idea.status)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = idea.category,
                fontSize = 12.sp,
                color = AguiaColors.TextSecondary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = idea.description,
                fontSize = 13.sp,
                color = AguiaColors.TextSecondary,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (!idea.updatedAt.isNullOrBlank()) "Atualizada em ${idea.updatedAt}" else "Enviada em ${idea.createdAt}",
                    fontSize = 12.sp,
                    color = AguiaColors.TextSecondary
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onClick() }
                ) {
                    Text(
                        text = "Ver detalhes",
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
