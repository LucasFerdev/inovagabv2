package br.com.inovagabv2.presentation.operator.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.inovagabv2.core.designsystem.AguiaColors
import br.com.inovagabv2.core.designsystem.components.*
import br.com.inovagabv2.core.navigation.Screen
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

    Scaffold(
        topBar = {
            AguiaTopBar(roleTag = "OPERADOR")
        },
        bottomBar = {
            AguiaBottomBar(
                currentRoute = Screen.OperatorHome.route,
                role = Role.OPERADOR,
                onNavigate = { route ->
                    when (route) {
                        Screen.OperatorHome.route -> { /* Already here */ }
                        Screen.MyIdeas.route -> onNavigateToMyIdeas()
                        Screen.OperatorCommunications.route -> onNavigateToCommunications()
                        Screen.Profile.route -> onNavigateToProfile()
                    }
                }
            )
        },
        containerColor = AguiaColors.Background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Greeting
            item {
                Column {
                    Text(
                        text = "Olá, ${if (state.userName.isNotBlank()) state.userName else "João"}!",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = AguiaColors.NavyDark
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Como você vai inovar hoje?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AguiaColors.TextSecondary
                    )
                }
            }

            // Summary 3 Cards Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OperatorMetricCard(
                        label = "Enviadas",
                        value = if (state.sentCount > 0) state.sentCount.toString() else "2",
                        icon = Icons.AutoMirrored.Filled.Send,
                        iconTintColor = AguiaColors.PrimaryBlue,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToMyIdeas
                    )
                    OperatorMetricCard(
                        label = "Em análise",
                        value = if (state.inAnalysisCount > 0) state.inAnalysisCount.toString() else "1",
                        icon = Icons.Default.Lightbulb,
                        iconTintColor = Color(0xFFD97706),
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToMyIdeas
                    )
                    OperatorMetricCard(
                        label = "Aprovadas",
                        value = if (state.approvedCount > 0) state.approvedCount.toString() else "1",
                        icon = Icons.Default.CheckCircle,
                        iconTintColor = Color(0xFF16A34A),
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToMyIdeas
                    )
                }
            }

            // Orientações estratégicas
            item {
                Column {
                    Text(
                        text = "Orientações estratégicas",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AguiaColors.NavyDark
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    if (state.strategies.isNotEmpty()) {
                        AguiaStrategyCard(
                            strategy = state.strategies.first(),
                            onClick = onNavigateToStrategies
                        )
                    }
                }
            }

            // Acompanhe suas sugestões
            item {
                Text(
                    text = "Acompanhe suas sugestões",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AguiaColors.NavyDark
                )
            }

            items(state.recentIdeas) { idea ->
                AguiaIdeaCard(
                    idea = idea,
                    onClick = { onNavigateToIdeaDetails(idea.id) }
                )
            }

            // Bottom Action Button: + Nova sugestão
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onNavigateToCreateIdea,
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
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Nova sugestão",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun OperatorMetricCard(
    label: String,
    value: String,
    icon: ImageVector,
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
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = label,
                fontSize = 11.sp,
                color = AguiaColors.TextSecondary,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTintColor,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
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
