package br.com.inovagabv2.presentation.manager.strategy

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.inovagabv2.domain.model.Role
import br.com.inovagabv2.presentation.operator.strategy.OperatorStrategyScreen
import br.com.inovagabv2.presentation.operator.strategy.OperatorStrategyViewModel

@Composable
fun ManagerStrategyScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToIdeas: () -> Unit,
    onNavigateToProjects: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onStrategyClick: (String) -> Unit,
    viewModel: OperatorStrategyViewModel = hiltViewModel()
) {
    OperatorStrategyScreen(
        viewModel = viewModel,
        userRole = Role.GESTOR,
        onBackClick = onNavigateToHome,
        onStrategyClick = onStrategyClick,
        onNavigateToHome = onNavigateToHome,
        onNavigateToSugestoes = onNavigateToIdeas,
        onNavigateToCommunications = onNavigateToProjects,
        onNavigateToProfile = onNavigateToProfile
    )
}
