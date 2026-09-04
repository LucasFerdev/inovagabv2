package br.com.inovagabv2.presentation.leadership

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.com.inovagabv2.core.designsystem.AguiaColors
import br.com.inovagabv2.core.designsystem.components.*
import br.com.inovagabv2.core.navigation.Screen
import br.com.inovagabv2.domain.model.Role

@Composable
fun LeadershipDashboardScreen(
    viewModel: LeadershipDashboardViewModel,
    onNavigateToProjects: () -> Unit,
    onNavigateToResults: () -> Unit,
    onNavigateToStrategy: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            AguiaTopBar(showLeadershipTag = true)
        },
        bottomBar = {
            AguiaBottomBar(
                currentRoute = Screen.LeadershipDashboard.route,
                role = Role.LIDERANCA,
                onNavigate = { route ->
                    when (route) {
                        Screen.LeadershipDashboard.route -> { /* Already here */ }
                        Screen.LeadershipProjects.route -> onNavigateToProjects()
                        Screen.LeadershipResults.route -> onNavigateToResults()
                        Screen.LeadershipStrategy.route -> onNavigateToStrategy()
                        Screen.Profile.route -> onNavigateToProfile()
                    }
                }
            )
        },
        containerColor = AguiaColors.Background
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AguiaColors.PrimaryBlue)
            }
        } else if (state.error != null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text(text = state.error ?: "Erro ao carregar dados", color = AguiaColors.ErrorRed)
            }
        } else {
            state.data?.let { data ->
                DashboardContent(
                    data = data,
                    modifier = Modifier.padding(padding),
                    onNavigateToProjects = onNavigateToProjects,
                    onNavigateToStrategy = onNavigateToStrategy
                )
            }
        }
    }
}

@Composable
private fun DashboardContent(
    data: br.com.inovagabv2.domain.model.DashboardData,
    modifier: Modifier = Modifier,
    onNavigateToProjects: () -> Unit,
    onNavigateToStrategy: () -> Unit
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp)
    ) {
        // Greeting Title
        item {
            Column {
                Text(
                    text = "Olá, Carlos!",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = AguiaColors.NavyDark
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Visão estratégica da inovação",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AguiaColors.TextSecondary
                )
            }
        }

        // 2x2 Grid Cards with Sparklines
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AguiaDashboardCard(
                        title = "ROI Global",
                        value = data.roi,
                        showSparkline = true,
                        isPositiveSparkline = true,
                        modifier = Modifier.weight(1f)
                    )
                    AguiaDashboardCard(
                        title = "Lucro Gerado",
                        value = data.profit,
                        showSparkline = true,
                        isPositiveSparkline = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AguiaDashboardCard(
                        title = "Redução de Custos",
                        value = data.costReduction,
                        showSparkline = true,
                        isPositiveSparkline = false,
                        modifier = Modifier.weight(1f)
                    )
                    AguiaDashboardCard(
                        title = "Produtividade",
                        value = data.productivity,
                        showSparkline = true,
                        isPositiveSparkline = true,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Section Title: Resumo operacional
        item {
            Text(
                text = "Resumo operacional",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AguiaColors.NavyDark,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // Operational Summary Cards
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OperationalSummaryCard(
                    title = "Projetos em andamento",
                    count = data.activeProjectsCount.toString(),
                    subtitle = "+2 vs. mês anterior",
                    icon = Icons.Default.Work,
                    iconBgColor = AguiaColors.PrimaryBlue.copy(alpha = 0.15f),
                    iconTintColor = AguiaColors.PrimaryBlue,
                    onClick = onNavigateToProjects
                )

                OperationalSummaryCard(
                    title = "Ideias aprovadas",
                    count = data.approvedIdeasCount.toString(),
                    subtitle = "+6 vs. mês anterior",
                    icon = Icons.Default.Lightbulb,
                    iconBgColor = Color(0xFFE0F2FE),
                    iconTintColor = Color(0xFF0284C7),
                    onClick = onNavigateToStrategy
                )
            }
        }
    }
}

@Composable
private fun OperationalSummaryCard(
    title: String,
    count: String,
    subtitle: String,
    icon: ImageVector,
    iconBgColor: Color,
    iconTintColor: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AguiaColors.CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTintColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = AguiaColors.TextSecondary,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = count,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = AguiaColors.NavyDark
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = AguiaColors.SuccessGreen,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
