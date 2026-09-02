package br.com.inovagabv2.presentation.operator.communications

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.com.inovagabv2.core.designsystem.AguiaColors
import br.com.inovagabv2.core.designsystem.components.AguiaBottomBar
import br.com.inovagabv2.core.designsystem.components.AguiaTopBar
import br.com.inovagabv2.core.navigation.Screen
import br.com.inovagabv2.domain.model.Role

data class Communication(
    val id: String,
    val title: String,
    val date: String,
    val description: String
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
            "Novas diretrizes de atendimento",
            "Hoje, 09:15",
            "Atualização importante sobre o procedimento de check-in rodoviário nas unidades da Viação Águia Branca."
        ),
        Communication(
            "2",
            "Reunião de Alinhamento (Manutenção)",
            "Ontem, 14:20",
            "Alinhamento geral com a equipe de manutenção sobre os novos ônibus G8."
        )
    )

    Scaffold(
        topBar = {
            AguiaTopBar(title = "Comunicações")
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
            items(communications) { communication ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = AguiaColors.CardWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = AguiaColors.PrimaryBlue,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = communication.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = AguiaColors.TextPrimary
                            )
                            Text(
                                text = communication.date,
                                style = MaterialTheme.typography.labelSmall,
                                color = AguiaColors.TextSecondary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = communication.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = AguiaColors.TextPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}
