package br.com.inovagabv2.presentation.operator.strategy

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.inovagabv2.core.designsystem.AguiaColors
import br.com.inovagabv2.core.designsystem.components.AguiaStrategyCard
import br.com.inovagabv2.core.designsystem.components.AguiaTextField
import br.com.inovagabv2.core.designsystem.components.AguiaTopBar

import br.com.inovagabv2.core.designsystem.components.*
import br.com.inovagabv2.core.navigation.Screen
import br.com.inovagabv2.domain.model.Role

@Composable
fun OperatorStrategyScreen(
    viewModel: OperatorStrategyViewModel,
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
            AguiaTopBar(
                title = "Estratégia",
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            AguiaBottomBar(
                currentRoute = Screen.OperatorStrategy.route,
                role = Role.OPERADOR,
                onNavigate = { route ->
                    when (route) {
                        Screen.OperatorHome.route -> onNavigateToHome()
                        Screen.MyIdeas.route -> onNavigateToSugestoes()
                        Screen.OperatorCommunications.route -> onNavigateToCommunications()
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
            AguiaTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                label = "Pesquisar orientações",
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.padding(16.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(strategies) { strategy ->
                    AguiaStrategyCard(
                        strategy = strategy,
                        onClick = { onStrategyClick(strategy.id) }
                    )
                }
                
                if (strategies.isEmpty()) {
                    item {
                        Text(
                            text = "Nenhuma orientação encontrada.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = AguiaColors.TextSecondary,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}
