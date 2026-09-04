package br.com.inovagabv2.presentation.operator.communications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.inovagabv2.core.designsystem.AguiaColors
import br.com.inovagabv2.core.designsystem.components.AguiaBottomBar
import br.com.inovagabv2.core.designsystem.components.AguiaTopBar
import br.com.inovagabv2.core.navigation.Screen
import br.com.inovagabv2.domain.model.Role

data class Communication(
    val id: String,
    val title: String,
    val description: String,
    val date: String,
    val category: String,
    val icon: ImageVector
)

@Composable
fun CommunicationsScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToSugestoes: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val communications = listOf(
        Communication(
            "1",
            "Semana da inovação",
            "Participe da nossa programação especial com palestras, desafios e muito mais!",
            "22/05/2025",
            "Inovação",
            Icons.Default.Campaign
        ),
        Communication(
            "2",
            "Novos ônibus da frota G8",
            "Conheça os novos veículos que estão chegando para oferecer mais conforto e segurança.",
            "15/05/2025",
            "Frota",
            Icons.Default.DirectionsBus
        ),
        Communication(
            "3",
            "Meta de sustentabilidade",
            "Confira nossos avanços e como cada um contribui para um futuro melhor.",
            "08/05/2025",
            "Sustentabilidade",
            Icons.Default.Eco
        )
    )

    Scaffold(
        topBar = {
            AguiaTopBar(roleTag = "OPERADOR")
        },
        bottomBar = {
            AguiaBottomBar(
                currentRoute = Screen.OperatorCommunications.route,
                role = Role.OPERADOR,
                onNavigate = { route ->
                    when (route) {
                        Screen.OperatorHome.route -> onNavigateToHome()
                        Screen.MyIdeas.route -> onNavigateToSugestoes()
                        Screen.OperatorCommunications.route -> { /* Already here */ }
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
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(communications) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = AguiaColors.CardWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(AguiaColors.PrimaryBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = AguiaColors.NavyDark
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = item.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = AguiaColors.TextSecondary
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = item.date,
                                    fontSize = 11.sp,
                                    color = AguiaColors.TextSecondary
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Icon(
                                    imageVector = Icons.Default.Folder,
                                    contentDescription = null,
                                    tint = AguiaColors.TextSecondary,
                                    modifier = Modifier.size(14.dp)
                                )

                                Spacer(modifier = Modifier.width(4.dp))

                                Text(
                                    text = item.category,
                                    fontSize = 11.sp,
                                    color = AguiaColors.TextSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
