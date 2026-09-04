package br.com.inovagabv2.presentation.operator.myideas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.com.inovagabv2.core.designsystem.AguiaColors
import br.com.inovagabv2.core.designsystem.components.*
import br.com.inovagabv2.core.navigation.Screen
import br.com.inovagabv2.domain.model.IdeaStatus
import br.com.inovagabv2.domain.model.Role

@Composable
fun MyIdeasScreen(
    viewModel: MyIdeasViewModel,
    onBackClick: () -> Unit,
    onIdeaClick: (String) -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToCommunications: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val ideas by viewModel.filteredIdeas.collectAsState()

    val filterTabs = listOf("Todas", "Enviadas", "Em análise", "Aprovadas")
    var selectedFilterIndex by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            AguiaTopBar(roleTag = "OPERADOR")
        },
        bottomBar = {
            AguiaBottomBar(
                currentRoute = Screen.MyIdeas.route,
                role = Role.OPERADOR,
                onNavigate = { route ->
                    when (route) {
                        Screen.OperatorHome.route -> onNavigateToHome()
                        Screen.MyIdeas.route -> { /* Already here */ }
                        Screen.OperatorCommunications.route -> onNavigateToCommunications()
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
            // Filter Pills Row
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filterTabs.size) { index ->
                    val isSelected = selectedFilterIndex == index
                    Surface(
                        onClick = {
                            selectedFilterIndex = index
                            val targetStatus = when (index) {
                                1 -> IdeaStatus.ENVIADA
                                2 -> IdeaStatus.EM_ANALISE
                                3 -> IdeaStatus.APROVADA
                                else -> null
                            }
                            viewModel.onFilterSelected(targetStatus)
                        },
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) AguiaColors.PrimaryBlue else AguiaColors.CardWhite,
                        shadowElevation = if (isSelected) 0.dp else 1.dp
                    ) {
                        Text(
                            text = filterTabs[index],
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else AguiaColors.TextPrimary,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(ideas) { idea ->
                    AguiaIdeaCard(
                        idea = idea,
                        onClick = { onIdeaClick(idea.id) }
                    )
                }

                if (ideas.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Nenhuma sugestão encontrada.",
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
