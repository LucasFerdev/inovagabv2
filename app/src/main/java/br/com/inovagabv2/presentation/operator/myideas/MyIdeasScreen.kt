package br.com.inovagabv2.presentation.operator.myideas

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.DirectionsBus
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.inovagabv2.core.designsystem.AguiaColors
import br.com.inovagabv2.core.designsystem.components.*
import br.com.inovagabv2.core.navigation.Screen
import br.com.inovagabv2.domain.model.Idea
import br.com.inovagabv2.domain.model.Role

@Composable
fun MyIdeasScreen(
    viewModel: MyIdeasViewModel,
    onBackClick: () -> Unit,
    onIdeaClick: (String) -> Unit,
    onNavigateToCreateIdea: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToCommunications: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val user by viewModel.user.collectAsState()
    val ideas by viewModel.ideas.collectAsState()
    val strategiesMap by viewModel.strategiesMap.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val currentRole = user?.role ?: Role.OPERADOR

    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    Scaffold(
        topBar = {
            AguiaTopBar(
                userName = user?.name,
                role = currentRole,
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            AguiaBottomBar(
                currentRoute = Screen.MyIdeas.route,
                role = currentRole,
                onNavigate = { route ->
                    when (route) {
                        Screen.OperatorHome.route, Screen.ManagerHome.route, Screen.LeadershipDashboard.route -> onNavigateToHome()
                        Screen.MyIdeas.route -> { /* Already here */ }
                        Screen.OperatorStrategy.route -> onNavigateToCommunications()
                        Screen.Profile.route -> onNavigateToProfile()
                        else -> onNavigateToCommunications()
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToCreateIdea,
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
                    text = "Nova ideia",
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
                // Page Header
                item {
                    Column {
                        Text(
                            text = "Minhas ideias",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = AguiaColors.NavyDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Acompanhe suas contribuições e veja cada etapa.",
                            fontSize = 14.sp,
                            color = AguiaColors.TextSecondary
                        )
                    }
                }

                // Hero Banner Card: X ideias enviadas (calculated dynamically)
                item {
                    val count = ideas.size
                    val countLabel = if (count == 1) "1 ideia enviada" else "$count ideias enviadas"

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                        border = BorderStroke(1.dp, Color(0xFFDBEAFE))
                    ) {
                        Row(
                            modifier = Modifier.padding(18.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFDBEAFE)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Lightbulb,
                                    contentDescription = null,
                                    tint = AguiaColors.PrimaryBlue,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column {
                                Text(
                                    text = countLabel,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AguiaColors.NavyDark
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Toque em uma ideia para ver os detalhes e o histórico.",
                                    fontSize = 12.sp,
                                    color = AguiaColors.TextSecondary,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }

                // Loading State
                if (isLoading) {
                    item {
                        AguiaLoadingState(message = "Carregando suas ideias...")
                    }
                } else if (ideas.isEmpty()) {
                    // Empty State
                    item {
                        AguiaEmptyState(
                            message = "Você ainda não enviou nenhuma ideia.",
                            actionText = "Criar primeira ideia",
                            onActionClick = onNavigateToCreateIdea
                        )
                    }
                } else {
                    // Ideas List
                    items(ideas, key = { it.id }) { idea ->
                        val strategyTitle = idea.strategyId?.let { strategiesMap[it] } ?: "Estratégia não identificada"
                        IdeaCardItem(
                            idea = idea,
                            strategyTitle = strategyTitle,
                            onClick = { onIdeaClick(idea.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun IdeaCardItem(
    idea: Idea,
    strategyTitle: String,
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
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            // Top Row: Square Icon + Title + Status Chip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Category Icon
                val icon = getCategoryIcon(idea.category)
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFEFF6FF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = AguiaColors.PrimaryBlue,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Status Chip
                AguiaStatusChip(status = idea.status)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Title
            Text(
                text = idea.title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = AguiaColors.NavyDark
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Category Subtitle
            Text(
                text = idea.category,
                fontSize = 13.sp,
                color = AguiaColors.TextSecondary
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Strategy Line
            Text(
                text = "Estratégia: $strategyTitle",
                fontSize = 12.sp,
                color = Color(0xFF64748B)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Bottom Row: Date + Chevron
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val dateText = when {
                    !idea.updatedAt.isNullOrBlank() -> "Atualizada em ${idea.updatedAt}"
                    else -> "Enviada em ${idea.createdAt}"
                }

                Text(
                    text = dateText,
                    fontSize = 12.sp,
                    color = AguiaColors.TextSecondary
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = AguiaColors.TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

private fun getCategoryIcon(category: String?): ImageVector {
    if (category == null) return Icons.Outlined.Lightbulb
    val cat = category.lowercase().trim()
    return when {
        cat.contains("operação") || cat.contains("embarque") || cat.contains("frota") -> Icons.Outlined.DirectionsBus
        cat.contains("sustentab") || cat.contains("coleta") || cat.contains("eco") -> Icons.Outlined.Eco
        cat.contains("manutenção") || cat.contains("checklist") -> Icons.Outlined.Build
        cat.contains("eficiência") || cat.contains("marcha") || cat.contains("tecnologia") -> Icons.Outlined.BarChart
        else -> Icons.Outlined.Lightbulb
    }
}
