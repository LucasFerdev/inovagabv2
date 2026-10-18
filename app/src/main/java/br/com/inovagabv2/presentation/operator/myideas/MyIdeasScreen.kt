package br.com.inovagabv2.presentation.operator.myideas

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.inovagabv2.core.designsystem.AguiaColors
import br.com.inovagabv2.core.designsystem.components.*
import br.com.inovagabv2.core.navigation.Screen
import br.com.inovagabv2.domain.model.Idea
import br.com.inovagabv2.domain.model.IdeaStatus
import br.com.inovagabv2.domain.model.Role
import br.com.inovagabv2.presentation.components.OnResumeEffect

@Composable
fun MyIdeasScreen(
    viewModel: MyIdeasViewModel,
    onBackClick: () -> Unit,
    onIdeaClick: (String) -> Unit,
    onNavigateToCreateIdea: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToStrategies: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val user by viewModel.user.collectAsState()
    val ideas by viewModel.ideas.collectAsState()
    val strategiesMap by viewModel.strategiesMap.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    var selectedStatus by remember { mutableStateOf<IdeaStatus?>(null) }

    val currentRole = user?.role ?: Role.OPERADOR

    val filteredIdeas = remember(ideas, selectedStatus) {
        if (selectedStatus == null) ideas
        else ideas.filter { it.status == selectedStatus }
    }

    OnResumeEffect(onResume = viewModel::refresh)

    Scaffold(
        topBar = {
            AguiaTopBar(
                userName = user?.name,
                role = currentRole,
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            AguiaBottomBar(
                currentRoute = Screen.MyIdeas.route,
                role = currentRole,
                onNavigate = { route ->
                    when (route) {
                        Screen.OperatorHome.route -> onNavigateToHome()
                        Screen.MyIdeas.route -> { /* Already here */ }
                        Screen.OperatorStrategy.route -> onNavigateToStrategies()
                        Screen.Profile.route -> onNavigateToProfile()
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToCreateIdea,
                containerColor = AguiaColors.PrimaryBlue,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Nova ideia", fontWeight = FontWeight.Bold) }
            )
        }
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
                            text = "Minhas ideias",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = AguiaColors.NavyDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Acompanhe o status e a evolução das suas propostas.",
                            fontSize = 14.sp,
                            color = AguiaColors.TextSecondary
                        )
                    }
                }

                // Filter Pills
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val isAllSelected = selectedStatus == null
                        StatusFilterPill(
                            label = "Todas",
                            isSelected = isAllSelected,
                            onClick = { selectedStatus = null }
                        )

                        listOf(
                            IdeaStatus.ENVIADA to "Enviadas",
                            IdeaStatus.EM_ANALISE to "Em análise",
                            IdeaStatus.APROVADA to "Aprovadas",
                            IdeaStatus.REJEITADA to "Rejeitadas"
                        ).forEach { (status, label) ->
                            val isSelected = selectedStatus == status
                            StatusFilterPill(
                                label = label,
                                isSelected = isSelected,
                                onClick = { selectedStatus = status }
                            )
                        }
                    }
                }

                // List Items
                if (isLoading) {
                    item {
                        AguiaLoadingState(message = "Carregando suas ideias...")
                    }
                } else if (error != null) {
                    item {
                        AguiaErrorState(
                            message = error ?: "Não foi possível carregar suas ideias. Tente novamente.",
                            onRetry = viewModel::refresh
                        )
                    }
                } else if (filteredIdeas.isEmpty()) {
                    item {
                        AguiaEmptyState(
                            message = if (selectedStatus == null)
                                "Você ainda não cadastrou nenhuma ideia. Clique em '+ Nova ideia' para enviar!"
                            else
                                "Nenhuma ideia encontrada com o filtro selecionado."
                        )
                    }
                } else {
                    items(filteredIdeas, key = { it.id }) { idea ->
                        val strategyTitle = strategiesMap[idea.strategyId]
                        MyIdeaCardItem(
                            idea = idea,
                            strategyTitle = strategyTitle,
                            onClick = { onIdeaClick(idea.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusFilterPill(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) AguiaColors.PrimaryBlue else Color.White,
        border = if (isSelected) null else BorderStroke(1.dp, Color(0xFF3B82F6))
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color.White else AguiaColors.PrimaryBlue,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun MyIdeaCardItem(
    idea: Idea,
    strategyTitle: String?,
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
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = idea.title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = AguiaColors.NavyDark,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                AguiaStatusChip(status = idea.status)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = idea.category,
                fontSize = 13.sp,
                color = AguiaColors.TextSecondary
            )

            if (!strategyTitle.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFEFF6FF)
                ) {
                    Text(
                        text = "Estratégia: $strategyTitle",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = AguiaColors.PrimaryBlue,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (!idea.createdAt.isNullOrBlank()) "Enviada em ${idea.createdAt}" else "",
                    fontSize = 12.sp,
                    color = AguiaColors.TextSecondary
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onClick() }
                ) {
                    Text(
                        text = "Ver detalhes",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = AguiaColors.PrimaryBlue
                    )
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = AguiaColors.PrimaryBlue,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
