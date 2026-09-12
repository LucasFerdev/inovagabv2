package br.com.inovagabv2.presentation.operator.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.outlined.TrackChanges
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import br.com.inovagabv2.R
import br.com.inovagabv2.core.designsystem.AguiaColors
import br.com.inovagabv2.core.designsystem.components.*
import br.com.inovagabv2.core.navigation.Screen
import br.com.inovagabv2.core.util.UserUtils
import br.com.inovagabv2.domain.model.Idea
import br.com.inovagabv2.domain.model.Role

@Composable
fun OperatorHomeScreen(
    viewModel: OperatorHomeViewModel,
    onNavigateToCreateIdea: () -> Unit,
    onNavigateToStrategies: () -> Unit,
    onNavigateToMyIdeas: () -> Unit,
    onNavigateToIdeaDetails: (String) -> Unit,
    onNavigateToCommunications: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val user by viewModel.user.collectAsState()

    val currentRole = user?.role ?: Role.OPERADOR
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
                currentRoute = Screen.OperatorHome.route,
                role = currentRole,
                onNavigate = { route ->
                    when (route) {
                        Screen.OperatorHome.route -> { /* Already here */ }
                        Screen.MyIdeas.route, Screen.ManagerIdeas.route -> onNavigateToMyIdeas()
                        Screen.OperatorStrategy.route -> onNavigateToStrategies()
                        Screen.Profile.route -> onNavigateToProfile()
                        else -> onNavigateToCommunications()
                    }
                }
            )
        },
        containerColor = Color.White
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Greeting Header
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
                        text = "Qual oportunidade você encontrou hoje?",
                        fontSize = 15.sp,
                        color = AguiaColors.TextSecondary
                    )
                }
            }

            // Main Banner Card (using R.drawable.home_banner)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Background Image
                        Image(
                            painter = painterResource(id = R.drawable.home_banner),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        // Dark Gradient Overlay for text readability
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.25f))
                        )

                        // Content Overlay
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(18.dp),
                            verticalArrangement = Arrangement.SpaceBetween,
                            horizontalAlignment = Alignment.Start
                        ) {
                            Column(modifier = Modifier.fillMaxWidth(0.75f)) {
                                Text(
                                    text = "Transforme uma observação em ideia",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    lineHeight = 24.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Pequenas ideias podem gerar grandes mudanças na nossa jornada.",
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.9f),
                                    lineHeight = 16.sp
                                )
                            }

                            Button(
                                onClick = onNavigateToCreateIdea,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = AguiaColors.PrimaryBlue,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.height(42.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Enviar nova ideia",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }

            // Section 1 Header: Continue acompanhando | Ver minhas ideias
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Continue acompanhando",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AguiaColors.NavyDark
                    )

                    Text(
                        text = "Ver minhas ideias",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = AguiaColors.PrimaryBlue,
                        modifier = Modifier.clickable { onNavigateToMyIdeas() }
                    )
                }
            }

            // Recent Ideas List or Empty State
            if (state.isLoading) {
                item {
                    AguiaLoadingState(message = "Carregando suas ideias...")
                }
            } else if (state.recentIdeas.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEFF6FF)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Lightbulb,
                                    contentDescription = null,
                                    tint = AguiaColors.PrimaryBlue,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Nenhuma ideia enviada ainda",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AguiaColors.NavyDark
                                )
                                Text(
                                    text = "Que tal cadastrar sua primeira sugestão?",
                                    fontSize = 12.sp,
                                    color = AguiaColors.TextSecondary
                                )
                            }
                        }
                    }
                }
            } else {
                items(state.recentIdeas, key = { it.id }) { idea ->
                    HomeIdeaCardItem(
                        idea = idea,
                        onClick = { onNavigateToIdeaDetails(idea.id) }
                    )
                }
            }

            // Section 2: Conheça as estratégias ativas Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToStrategies() },
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
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFDBEAFE)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.TrackChanges,
                                contentDescription = null,
                                tint = AguiaColors.PrimaryBlue,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Conheça as estratégias ativas",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = AguiaColors.NavyDark
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Veja onde suas ideias podem gerar mais impacto.",
                                fontSize = 12.sp,
                                color = AguiaColors.TextSecondary
                            )
                        }

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
}

@Composable
private fun HomeIdeaCardItem(
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val icon = getCategoryIcon(idea.category)
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFEFF6FF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = AguiaColors.PrimaryBlue,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = idea.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = AguiaColors.NavyDark
                )
                Spacer(modifier = Modifier.height(2.dp))
                val dateText = when {
                    !idea.updatedAt.isNullOrBlank() -> "Atualizada em ${idea.updatedAt}"
                    else -> "Enviada em ${idea.createdAt}"
                }
                Text(
                    text = dateText,
                    fontSize = 12.sp,
                    color = AguiaColors.TextSecondary
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            AguiaStatusChip(status = idea.status)

            Spacer(modifier = Modifier.width(4.dp))

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = AguiaColors.TextSecondary,
                modifier = Modifier.size(18.dp)
            )
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
