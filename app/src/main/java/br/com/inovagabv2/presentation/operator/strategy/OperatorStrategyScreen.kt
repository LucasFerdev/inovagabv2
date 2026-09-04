package br.com.inovagabv2.presentation.operator.strategy

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.inovagabv2.core.designsystem.AguiaColors
import br.com.inovagabv2.core.designsystem.components.*
import br.com.inovagabv2.core.navigation.Screen
import br.com.inovagabv2.domain.model.Role

@Composable
fun OperatorStrategyScreen(
    viewModel: OperatorStrategyViewModel,
    userRole: Role = Role.GESTOR,
    onBackClick: () -> Unit,
    onStrategyClick: (String) -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToSugestoes: () -> Unit,
    onNavigateToCommunications: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val strategies by viewModel.filteredStrategies.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    Scaffold(
        topBar = {
            if (userRole == Role.GESTOR) {
                AguiaTopBar(roleTag = "GESTOR")
            } else {
                AguiaTopBar(
                    title = "Estratégia",
                    onBackClick = onBackClick
                )
            }
        },
        bottomBar = {
            AguiaBottomBar(
                currentRoute = Screen.OperatorStrategy.route,
                role = userRole,
                onNavigate = { route ->
                    when (route) {
                        Screen.OperatorHome.route, Screen.ManagerHome.route -> onNavigateToHome()
                        Screen.MyIdeas.route, Screen.ManagerIdeas.route -> onNavigateToSugestoes()
                        Screen.OperatorCommunications.route, Screen.ManagerProjects.route -> onNavigateToCommunications()
                        Screen.OperatorStrategy.route -> { /* Already here */ }
                        Screen.Profile.route -> onNavigateToProfile()
                    }
                }
            )
        },
        containerColor = AguiaColors.Background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                placeholder = { Text("Pesquisar estratégias...", color = AguiaColors.TextSecondary, fontSize = 14.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = AguiaColors.TextSecondary) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = AguiaColors.CardWhite,
                    unfocusedContainerColor = AguiaColors.CardWhite,
                    focusedBorderColor = AguiaColors.PrimaryBlue.copy(alpha = 0.3f),
                    unfocusedBorderColor = Color.Transparent
                ),
                singleLine = true
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(strategies) { strategy ->
                    AguiaStrategyCard(
                        strategy = strategy,
                        onClick = { onStrategyClick(strategy.id) }
                    )
                }
                
                if (strategies.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Nenhuma estratégia encontrada.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = AguiaColors.TextSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}
