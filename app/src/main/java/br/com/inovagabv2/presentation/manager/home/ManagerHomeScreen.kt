package br.com.inovagabv2.presentation.manager.home

import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.inovagabv2.R
import br.com.inovagabv2.core.designsystem.AguiaColors
import br.com.inovagabv2.core.designsystem.components.*
import br.com.inovagabv2.core.navigation.Screen
import br.com.inovagabv2.core.util.UserUtils
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
    val firstName = UserUtils.getFirstName(state.user?.name)

    Scaffold(
        topBar = {
            AguiaTopBar(
                userName = state.user?.name,
                role = Role.GESTOR,
                showRoleBadge = true
            )
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
        containerColor = Color.White
    ) { padding ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                AguiaLoadingState(message = "Carregando resumo...")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Greeting
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
                            text = "Confira o resumo da inovação hoje.",
                            fontSize = 15.sp,
                            color = AguiaColors.TextSecondary
                        )
                    }
                }

                // Gestor Banner
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp),
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.gestor_banner),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                // Metrics 2x2 Grid
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            GestorMetricCard(
                                label = "Recebidas",
                                value = state.metrics.receivedIdeas.toString(),
                                icon = Icons.Default.Lightbulb,
                                iconBgColor = Color(0xFFE0F2FE),
                                iconTintColor = Color(0xFF0284C7),
                                modifier = Modifier.weight(1f),
                                onClick = onNavigateToIdeas
                            )
                            GestorMetricCard(
                                label = "Em análise",
                                value = state.metrics.inAnalysisIdeas.toString(),
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
                                value = state.metrics.approvedIdeas.toString(),
                                icon = Icons.Default.Check,
                                iconBgColor = Color(0xFFDCFCE7),
                                iconTintColor = Color(0xFF16A34A),
                                modifier = Modifier.weight(1f),
                                onClick = onNavigateToIdeas
                            )
                            GestorMetricCard(
                                label = "Projetos",
                                value = state.metrics.activeProjects.toString(),
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
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = AguiaColors.NavyDark
                        )
                        Text(
                            text = "Ver todas",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = AguiaColors.PrimaryBlue,
                            modifier = Modifier.clickable { onNavigateToIdeas() }
                        )
                    }
                }

                // Ideas List or Empty State
                if (state.ideas.isEmpty()) {
                    item {
                        AguiaEmptyState(
                            message = "Nenhuma ideia aguardando sua avaliação."
                        )
                    }
                } else {
                    items(state.ideas, key = { it.id }) { idea ->
                        AguiaIdeaCard(
                            idea = idea,
                            onClick = { onNavigateToIdeaDetails(idea.id) }
                        )
                    }
                }

                // Bottom Action Button: Analisar ideias
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = onNavigateToIdeas,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
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
                            fontSize = 16.sp
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
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEEF2F6))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
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
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = AguiaColors.NavyDark
                )
            }
        }
    }
}
