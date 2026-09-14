package br.com.inovagabv2.presentation.operator.strategy

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.outlined.TrackChanges
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.inovagabv2.core.designsystem.AguiaColors
import br.com.inovagabv2.core.designsystem.components.*
import br.com.inovagabv2.core.navigation.Screen
import br.com.inovagabv2.domain.model.Role
import br.com.inovagabv2.domain.model.Strategy

@Composable
fun OperatorStrategyScreen(
    viewModel: OperatorStrategyViewModel,
    userRole: Role = Role.OPERADOR,
    onBackClick: () -> Unit,
    onStrategyClick: (String) -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToSugestoes: () -> Unit,
    onNavigateToCommunications: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val user by viewModel.user.collectAsState()
    val strategies by viewModel.filteredStrategies.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val currentRole = user?.role ?: userRole

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
                currentRoute = if (currentRole == Role.GESTOR) Screen.ManagerStrategy.route else Screen.OperatorStrategy.route,
                role = currentRole,
                onNavigate = { route ->
                    when (route) {
                        Screen.OperatorHome.route, Screen.ManagerHome.route, Screen.LeadershipDashboard.route -> onNavigateToHome()
                        Screen.MyIdeas.route, Screen.ManagerIdeas.route -> onNavigateToSugestoes()
                        Screen.OperatorStrategy.route, Screen.ManagerStrategy.route, Screen.LeadershipStrategy.route -> { /* Already here */ }
                        Screen.Profile.route -> onNavigateToProfile()
                        else -> onNavigateToCommunications()
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
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Page Header
                item {
                    Column {
                        Text(
                            text = "Estratégias",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = AguiaColors.NavyDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Conheça os direcionamentos que guiam a inovação no Grupo.",
                            fontSize = 14.sp,
                            color = AguiaColors.TextSecondary
                        )
                    }
                }

                // Hero Banner Card: X estratégias ativas
                item {
                    val activeCount = strategies.size
                    val countLabel = if (activeCount == 1) "1 estratégia ativa" else "$activeCount estratégias ativas"

                    Card(
                        modifier = Modifier.fillMaxWidth(),
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
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFDBEAFE)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.TrackChanges,
                                    contentDescription = null,
                                    tint = AguiaColors.PrimaryBlue,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column {
                                Text(
                                    text = countLabel,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AguiaColors.NavyDark
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Use estes direcionamentos para conectar sua ideia às prioridades do Grupo.",
                                    fontSize = 12.sp,
                                    color = AguiaColors.TextSecondary,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }

                // Category Filter Pills
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val isAllSelected = selectedCategory == null
                        FilterChipPill(
                            label = "Todas",
                            isSelected = isAllSelected,
                            onClick = { viewModel.onCategorySelected(null) }
                        )

                        categories.forEach { category ->
                            val isSelected = selectedCategory.equals(category, ignoreCase = true)
                            FilterChipPill(
                                label = category,
                                isSelected = isSelected,
                                onClick = { viewModel.onCategorySelected(category) }
                            )
                        }
                    }
                }

                // Section Title
                item {
                    Text(
                        text = "Direcionamentos ativos",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AguiaColors.NavyDark,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                // Loading / Empty / Success List
                if (isLoading) {
                    item {
                        AguiaLoadingState(message = "Carregando estratégias...")
                    }
                } else if (strategies.isEmpty()) {
                    item {
                        AguiaEmptyState(
                            message = "Nenhuma estratégia ativa disponível no momento."
                        )
                    }
                } else {
                    items(strategies, key = { it.id }) { strategy ->
                        StrategyCardItem(
                            strategy = strategy,
                            onClick = { onStrategyClick(strategy.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterChipPill(
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
private fun StrategyCardItem(
    strategy: Strategy,
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
            // Header Row: Square Category Icon on left, Status Chip on right
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                val icon = getStrategyIcon(strategy.category)
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFEFF6FF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = AguiaColors.PrimaryBlue,
                        modifier = Modifier.size(24.dp)
                    )
                }

                AguiaStatusChip(statusText = "ATIVA")
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = strategy.title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = AguiaColors.NavyDark,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = AguiaColors.TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Description
            Text(
                text = strategy.description,
                fontSize = 13.sp,
                color = AguiaColors.TextSecondary,
                lineHeight = 18.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Meta row with formatted Brazilian date (dd/MM/yyyy)
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        strategy.category?.let { cat ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.LocalOffer,
                                    contentDescription = null,
                                    tint = AguiaColors.NavyDark,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = cat,
                                    fontSize = 12.sp,
                                    color = AguiaColors.NavyDark,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        strategy.campaign?.let { camp ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.Flag,
                                    contentDescription = null,
                                    tint = AguiaColors.NavyDark,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = camp,
                                    fontSize = 12.sp,
                                    color = AguiaColors.NavyDark,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

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

                val formattedDate = formatDatePtBr(strategy.publishedDate ?: strategy.date)
                if (formattedDate.isNotBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.CalendarToday,
                            contentDescription = null,
                            tint = AguiaColors.TextSecondary,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = formattedDate,
                            fontSize = 12.sp,
                            color = AguiaColors.TextSecondary
                        )
                    }
                }
            }
        }
    }
}

private fun formatDatePtBr(isoDate: String?): String {
    if (isoDate.isNullOrBlank()) return ""
    return try {
        val parts = isoDate.split("-")
        if (parts.size >= 3) {
            "${parts[2].take(2)}/${parts[1]}/${parts[0]}"
        } else isoDate
    } catch (_: Exception) {
        isoDate
    }
}

private fun getStrategyIcon(category: String?): ImageVector {
    if (category == null) return Icons.Outlined.TrackChanges
    val cat = category.lowercase().trim()
    return when {
        cat.contains("experiência") || cat.contains("cliente") -> Icons.Outlined.TrackChanges
        cat.contains("sustentab") || cat.contains("eco") -> Icons.Outlined.Eco
        cat.contains("inova") || cat.contains("propósito") -> Icons.Outlined.Lightbulb
        else -> Icons.Outlined.TrackChanges
    }
}
