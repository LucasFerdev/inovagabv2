package br.com.inovagabv2.presentation.leadership

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material.icons.outlined.Work
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.inovagabv2.core.designsystem.AguiaColors
import br.com.inovagabv2.core.designsystem.components.*
import br.com.inovagabv2.core.navigation.Screen
import br.com.inovagabv2.core.util.UserUtils
import br.com.inovagabv2.domain.model.DashboardData
import br.com.inovagabv2.domain.model.ProjectStatus
import br.com.inovagabv2.domain.model.Role

@Composable
fun LeadershipDashboardScreen(
    viewModel: LeadershipDashboardViewModel,
    onNavigateToProjects: () -> Unit,
    onNavigateToIdeas: () -> Unit,
    onNavigateToStrategy: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val user by viewModel.user.collectAsState()

    val currentRole = user?.role ?: Role.LIDERANCA
    val firstName = UserUtils.getFirstName(user?.name)

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
                        Screen.LeadershipDashboard.route -> { /* Already here */ }
                        Screen.LeadershipProjects.route, Screen.ManagerProjects.route -> onNavigateToProjects()
                        Screen.LeadershipStrategy.route, Screen.OperatorStrategy.route -> onNavigateToStrategy()
                        Screen.Profile.route -> onNavigateToProfile()
                        else -> onNavigateToIdeas()
                    }
                }
            )
        },
        containerColor = Color.White
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                AguiaLoadingState(message = "Carregando indicadores...")
            }
        } else if (state.error != null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                AguiaErrorState(
                    message = state.error ?: "Erro ao carregar dados",
                    onRetry = { viewModel.loadDashboardData() }
                )
            }
        } else {
            val data = state.data
            if (data == null) {
                AguiaEmptyState(
                    message = "Ainda não há dados suficientes para o Dashboard.",
                    modifier = Modifier.padding(padding)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Header Title
                    item {
                        Column {
                            Text(
                                text = "Olá, $firstName!",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = AguiaColors.NavyDark
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Decisões que transformam ideias em resultados.",
                                fontSize = 14.sp,
                                color = AguiaColors.TextSecondary
                            )
                        }
                    }

                    // Main Executive Card (Dark blue gradient)
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        brush = Brush.verticalGradient(
                                            colors = listOf(
                                                Color(0xFF0F2B5B),
                                                Color(0xFF06162F)
                                            )
                                        ),
                                        shape = RoundedCornerShape(20.dp)
                                    )
                                    .padding(20.dp)
                            ) {
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Visão executiva",
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )

                                        // Green ROI pill
                                        Surface(
                                            shape = RoundedCornerShape(20.dp),
                                            color = Color(0xFF064E3B)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Outlined.TrendingUp,
                                                    contentDescription = null,
                                                    tint = Color(0xFF34D399),
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "ROI ${data.roi}",
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF34D399)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))

                                    Text(
                                        text = "Resultado consolidado",
                                        fontSize = 12.sp,
                                        color = Color.White.copy(alpha = 0.8f)
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = data.profit,
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))
                                    HorizontalDivider(color = Color.White.copy(alpha = 0.15f))
                                    Spacer(modifier = Modifier.height(14.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text(
                                                text = "Investimento",
                                                fontSize = 11.sp,
                                                color = Color.White.copy(alpha = 0.7f)
                                            )
                                            Text(
                                                text = formatMonetary(data.investment),
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }

                                        Column {
                                            Text(
                                                text = "Retorno",
                                                fontSize = 11.sp,
                                                color = Color.White.copy(alpha = 0.7f)
                                            )
                                            Text(
                                                text = formatMonetary(data.financialReturn),
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Section Title: Indicadores
                    item {
                        Text(
                            text = "Indicadores",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = AguiaColors.NavyDark
                        )
                    }

                    // 3 Metric Cards Row
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            ExecutiveMetricCard(
                                value = data.activeProjectsCount.toString(),
                                label = "Projetos",
                                icon = Icons.Outlined.Work,
                                iconColor = AguiaColors.PrimaryBlue,
                                modifier = Modifier.weight(1f),
                                onClick = onNavigateToProjects
                            )

                            ExecutiveMetricCard(
                                value = data.delayedProjectsCount.toString(),
                                label = "Atrasados",
                                icon = Icons.Outlined.Schedule,
                                iconColor = Color(0xFFD97706),
                                modifier = Modifier.weight(1f),
                                onClick = onNavigateToProjects
                            )

                            ExecutiveMetricCard(
                                value = data.approvedIdeasCount.toString(),
                                label = "Ideias aprovadas",
                                icon = Icons.Outlined.Lightbulb,
                                iconColor = Color(0xFF16A34A),
                                modifier = Modifier.weight(1f),
                                onClick = onNavigateToIdeas
                            )
                        }
                    }

                    // Section: Portfólio de projetos
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color(0xFFEEF2F6))
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Portfólio de projetos",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AguiaColors.NavyDark
                                    )

                                    Text(
                                        text = "Ver detalhes",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AguiaColors.PrimaryBlue,
                                        modifier = Modifier.clickable { onNavigateToProjects() }
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                val totalProjects = data.projetosPorStatus.values.sum().toInt().coerceAtLeast(1)
                                val inProgress = (data.projetosPorStatus[ProjectStatus.EM_ANDAMENTO] ?: 0L).toInt()
                                val planned = (data.projetosPorStatus[ProjectStatus.PLANEJADO] ?: 0L).toInt()
                                val completed = (data.projetosPorStatus[ProjectStatus.CONCLUIDO] ?: 0L).toInt()
                                val delayed = data.projetosAtrasados.toInt()

                                PortfolioBarRow("Em andamento", inProgress, totalProjects, Color(0xFF2563EB))
                                Spacer(modifier = Modifier.height(10.dp))
                                PortfolioBarRow("Planejados", planned, totalProjects, Color(0xFF38BDF8))
                                Spacer(modifier = Modifier.height(10.dp))
                                PortfolioBarRow("Concluídos", completed, totalProjects, Color(0xFF16A34A))
                                Spacer(modifier = Modifier.height(10.dp))
                                PortfolioBarRow("Atrasados", delayed, totalProjects, Color(0xFFF59E0B))
                            }
                        }
                    }

                    // Action Shortcut Card 1: Ideias para acompanhamento
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigateToIdeas() },
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                            border = BorderStroke(1.dp, Color(0xFFDBEAFE))
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFDBEAFE)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Lightbulb,
                                        contentDescription = null,
                                        tint = AguiaColors.PrimaryBlue,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Ideias para acompanhamento",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AguiaColors.NavyDark
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${data.inAnalysisIdeasCount} aguardam avaliação da gestão",
                                        fontSize = 12.sp,
                                        color = AguiaColors.TextSecondary
                                    )
                                }

                                Text(
                                    text = "Acompanhar",
                                    fontSize = 12.sp,
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

                    // Action Shortcut Card 2: Projetos que exigem atenção
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigateToProjects() },
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                            border = BorderStroke(1.dp, Color(0xFFFDE68A))
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFFDE68A)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Warning,
                                        contentDescription = null,
                                        tint = Color(0xFFD97706),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Projetos que exigem atenção",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AguiaColors.NavyDark
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${data.delayedProjectsCount} atrasados • acompanhar prazos",
                                        fontSize = 12.sp,
                                        color = AguiaColors.TextSecondary
                                    )
                                }

                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = null,
                                    tint = AguiaColors.NavyDark,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ExecutiveMetricCard(
    value: String,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier.then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFEEF2F6))
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
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

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = AguiaColors.NavyDark
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = label,
                fontSize = 11.sp,
                color = AguiaColors.TextSecondary,
                lineHeight = 14.sp
            )
        }
    }
}

@Composable
private fun PortfolioBarRow(
    label: String,
    count: Int,
    total: Int,
    barColor: Color
) {
    val progress = (count.toFloat() / total.toFloat()).coerceIn(0f, 1f)

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = AguiaColors.NavyDark,
            modifier = Modifier.width(105.dp)
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFFF1F5F9))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress)
                    .clip(RoundedCornerShape(4.dp))
                    .background(barColor)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = count.toString(),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = AguiaColors.NavyDark
        )
    }
}

private fun formatMonetary(value: Double): String {
    return when {
        value >= 1_000_000 -> String.format(java.util.Locale.US, "R$ %.2f mi", value / 1_000_000).replace(".", ",")
        value >= 1_000 -> String.format(java.util.Locale.US, "R$ %.0f mil", value / 1_000)
        else -> String.format(java.util.Locale.US, "R$ %.2f", value).replace(".", ",")
    }
}
