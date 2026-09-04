package br.com.inovagabv2.presentation.manager.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Schedule
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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.inovagabv2.core.designsystem.AguiaColors
import br.com.inovagabv2.core.designsystem.components.*
import br.com.inovagabv2.core.navigation.Screen
import br.com.inovagabv2.domain.model.Role

@Composable
fun ManagerHomeScreen(
    onNavigateToIdeas: () -> Unit,
    onNavigateToIdeaDetails: (String) -> Unit,
    onNavigateToProjects: () -> Unit,
    onNavigateToStrategy: () -> Unit,
    onNavigateToProfile: () -> Unit,
    viewModel: ManagerHomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            AguiaTopBar(roleTag = "GESTOR")
        },
        bottomBar = {
            AguiaBottomBar(
                currentRoute = Screen.ManagerHome.route,
                role = Role.GESTOR,
                onNavigate = { route ->
                    when (route) {
                        Screen.ManagerHome.route -> { /* Already here */ }
                        Screen.ManagerIdeas.route -> onNavigateToIdeas()
                        Screen.ManagerProjects.route -> onNavigateToProjects()
                        Screen.OperatorStrategy.route -> onNavigateToStrategy()
                        Screen.Profile.route -> onNavigateToProfile()
                    }
                }
            )
        },
        containerColor = AguiaColors.Background
    ) { padding ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AguiaColors.PrimaryBlue)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp)
            ) {
                // Greeting
                item {
                    Column {
                        Text(
                            text = "Olá, ${state.user?.name ?: "Mariana"}!",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = AguiaColors.NavyDark
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Confira o resumo da inovação hoje.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = AguiaColors.TextSecondary
                        )
                    }
                }

                // Metrics 2x2 Grid
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            GestorMetricCard(
                                label = "Recebidas",
                                value = if (state.metrics.receivedIdeas > 0) state.metrics.receivedIdeas.toString() else "24",
                                icon = Icons.Default.Lightbulb,
                                iconBgColor = Color(0xFFE0F2FE),
                                iconTintColor = Color(0xFF0284C7),
                                modifier = Modifier.weight(1f),
                                onClick = onNavigateToIdeas
                            )
                            GestorMetricCard(
                                label = "Em análise",
                                value = if (state.metrics.inAnalysisIdeas > 0) state.metrics.inAnalysisIdeas.toString() else "8",
                                icon = Icons.Default.Schedule,
                                iconBgColor = Color(0xFFFEF3C7),
                                iconTintColor = Color(0xFFD97706),
                                modifier = Modifier.weight(1f),
                                onClick = onNavigateToIdeas
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            GestorMetricCard(
                                label = "Aprovadas",
                                value = if (state.metrics.approvedIdeas > 0) state.metrics.approvedIdeas.toString() else "10",
                                icon = Icons.Default.Check,
                                iconBgColor = Color(0xFFDCFCE7),
                                iconTintColor = Color(0xFF16A34A),
                                modifier = Modifier.weight(1f),
                                onClick = onNavigateToIdeas
                            )
                            GestorMetricCard(
                                label = "Projetos",
                                value = if (state.metrics.activeProjects > 0) state.metrics.activeProjects.toString() else "6",
                                icon = Icons.Default.Work,
                                iconBgColor = Color(0xFFE0F2FE),
                                iconTintColor = Color(0xFF0284C7),
                                modifier = Modifier.weight(1f),
                                onClick = onNavigateToProjects
                            )
                        }
                    }
                }

                // Section Title: Ideias para análise | Ver todas
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Ideias para análise",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AguiaColors.NavyDark
                        )
                        TextButton(onClick = onNavigateToIdeas) {
                            Text(
                                text = "Ver todas",
                                color = AguiaColors.PrimaryBlue,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // Ideas List
                items(state.ideas.take(3)) { idea ->
                    AguiaIdeaCard(
                        idea = idea,
                        onClick = { onNavigateToIdeaDetails(idea.id) }
                    )
                }

                // Bottom Action Button
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = onNavigateToIdeas,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AguiaColors.PrimaryBlue,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Analisar ideias",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GestorMetricCard(
    label: String,
    value: String,
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
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTintColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = label,
                    fontSize = 11.sp,
                    color = AguiaColors.TextSecondary,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = AguiaColors.NavyDark
                )
            }
        }
    }
}
