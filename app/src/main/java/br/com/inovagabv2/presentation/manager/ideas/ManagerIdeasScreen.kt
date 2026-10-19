package br.com.inovagabv2.presentation.manager.ideas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.inovagabv2.core.designsystem.AguiaColors
import br.com.inovagabv2.core.designsystem.components.*
import br.com.inovagabv2.core.navigation.Screen
import br.com.inovagabv2.domain.model.Role
import br.com.inovagabv2.presentation.components.OnResumeEffect
import br.com.inovagabv2.presentation.session.rememberLoggedUserName

@Composable
fun ManagerIdeasScreen(
    onNavigateToDetails: (String) -> Unit,
    onBackClick: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToProjects: () -> Unit,
    onNavigateToStrategy: () -> Unit,
    onNavigateToProfile: () -> Unit,
    viewModel: ManagerIdeasViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    OnResumeEffect(onResume = viewModel::refresh)

    Scaffold(
        topBar = {
            AguiaTopBar(
                userName = rememberLoggedUserName(),
                role = Role.GESTOR,
                showRoleBadge = true
            )
        },
        bottomBar = {
            AguiaBottomBar(
                currentRoute = Screen.ManagerIdeas.route,
                role = Role.GESTOR,
                onNavigate = { route ->
                    when (route) {
                        Screen.ManagerHome.route -> onNavigateToHome()
                        Screen.ManagerIdeas.route -> { /* Already here */ }
                        Screen.ManagerProjects.route -> onNavigateToProjects()
                        Screen.ManagerStrategy.route -> onNavigateToStrategy()
                        Screen.Profile.route -> onNavigateToProfile()
                    }
                }
            )
        },
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Page Title
                item {
                    Column {
                        Text(
                            text = "Avaliação de Ideias",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = AguiaColors.NavyDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Analise e delibere sobre as propostas enviadas pela equipe.",
                            fontSize = 14.sp,
                            color = AguiaColors.TextSecondary
                        )
                    }
                }

                if (state.isLoading && state.ideas.isEmpty()) {
                    item {
                        AguiaLoadingState(message = "Carregando ideias para avaliação...")
                    }
                } else if (state.error != null) {
                    item {
                        AguiaErrorState(
                            message = state.error ?: "Não foi possível carregar as ideias. Tente novamente.",
                            onRetry = viewModel::refresh
                        )
                    }
                } else if (state.ideas.isEmpty()) {
                    item {
                        AguiaEmptyState(message = "Nenhuma ideia cadastrada no momento.")
                    }
                } else {
                    items(state.ideas, key = { it.id }) { idea ->
                        AguiaIdeaCard(
                            idea = idea,
                            onClick = { onNavigateToDetails(idea.id) }
                        )
                    }
                }
            }
        }
    }
}
