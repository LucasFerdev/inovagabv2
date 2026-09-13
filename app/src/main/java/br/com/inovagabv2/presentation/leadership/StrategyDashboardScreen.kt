package br.com.inovagabv2.presentation.leadership

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.outlined.TrackChanges
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.inovagabv2.core.designsystem.AguiaColors
import br.com.inovagabv2.core.designsystem.components.*
import br.com.inovagabv2.core.navigation.Screen
import br.com.inovagabv2.domain.model.Idea
import br.com.inovagabv2.domain.model.IdeaStatus
import br.com.inovagabv2.domain.model.Role
import br.com.inovagabv2.domain.model.Strategy

@Composable
fun StrategyDashboardScreen(
    onBackClick: () -> Unit,
    onIdeaClick: (String) -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToProjects: () -> Unit,
    onNavigateToIdeas: () -> Unit,
    onNavigateToProfile: () -> Unit,
    viewModel: StrategyDashboardViewModel = hiltViewModel()
) {
    val user by viewModel.user.collectAsState()
    val strategy by viewModel.strategy.collectAsState()
    val linkedIdeas by viewModel.linkedIdeas.collectAsState()
    val linkedProjects by viewModel.linkedProjects.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val currentRole = user?.role ?: Role.LIDERANCA

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
                currentRoute = Screen.LeadershipDashboard.route,
                role = currentRole,
                onNavigate = { route ->
                    when (route) {
                        Screen.LeadershipDashboard.route -> onNavigateToHome()
                        Screen.LeadershipProjects.route -> onNavigateToProjects()
                        Screen.LeadershipStrategy.route -> { /* Already here */ }
                        Screen.Profile.route -> onNavigateToProfile()
                        else -> onNavigateToIdeas()
                    }
                }
            )
        },
        containerColor = Color.White
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                AguiaLoadingState(message = "Carregando painel...")
            }
        } else if (strategy == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                AguiaEmptyState(message = "Estratégia não encontrada.")
            }
        } else {
            val currentStrategy = strategy!!
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Strategy Header Title & Status
                item {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = currentStrategy.title,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold,
                                color = AguiaColors.NavyDark,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            AguiaStatusChip(statusText = if (currentStrategy.isPublished) "ATIVA" else "RASCUNHO")
                        }

                        currentStrategy.campaign?.let { camp ->
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Campanha: $camp",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = AguiaColors.PrimaryBlue
                            )
                        }
                    }
                }

                // Card 1: Detalhes da estratégia
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                        border = BorderStroke(1.dp, Color(0xFFDBEAFE))
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFDBEAFE)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.TrackChanges,
                                        contentDescription = null,
                                        tint = AguiaColors.PrimaryBlue,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = "Detalhes da estratégia",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AguiaColors.NavyDark
                                    )

                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        currentStrategy.category?.let { cat ->
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Outlined.LocalOffer, contentDescription = null, tint = AguiaColors.NavyDark, modifier = Modifier.size(12.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(text = "Categoria: $cat", fontSize = 11.sp, color = AguiaColors.NavyDark)
                                            }
                                        }

                                        currentStrategy.publishedDate?.let { date ->
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Outlined.CalendarToday, contentDescription = null, tint = AguiaColors.TextSecondary, modifier = Modifier.size(12.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(text = "Início: $date", fontSize = 11.sp, color = AguiaColors.TextSecondary)
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = currentStrategy.description,
                                fontSize = 13.sp,
                                color = AguiaColors.TextSecondary,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }

                // Card 2: Resultado consolidado
                item {
                    val totalInvest = linkedProjects.sumOf { parseCurrencyNum(it.investment) }
                    val totalReturn = linkedProjects.sumOf { parseCurrencyNum(it.description) }
                    val profit = (totalReturn - totalInvest).coerceAtLeast(0.0)
                    val roiVal = if (totalInvest > 0) ((totalReturn - totalInvest) / totalInvest) * 100 else 0.0

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFEEF2F6))
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Resultado consolidado",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AguiaColors.NavyDark
                                )

                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = Color(0xFFDCFCE7)
                                ) {
                                    Text(
                                        text = String.format(java.util.Locale.US, "ROI %.1f%%", roiVal).replace(".", ","),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF16A34A),
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                ConsolidatedMetricCol("Investimento", formatMonetaryVal(totalInvest), AguiaColors.PrimaryBlue)
                                ConsolidatedMetricCol("Retorno", formatMonetaryVal(totalReturn), Color(0xFF0284C7))
                                ConsolidatedMetricCol("Lucro", formatMonetaryVal(profit), Color(0xFF16A34A))
                            }
                        }
                    }
                }

                // Card 3: Ideias por status (Donut Chart)
                item {
                    val inAnalysis = linkedIdeas.count { it.status == IdeaStatus.EM_ANALISE }
                    val approved = linkedIdeas.count { it.status == IdeaStatus.APROVADA }
                    val rejected = linkedIdeas.count { it.status == IdeaStatus.REJEITADA }
                    val sent = linkedIdeas.count { it.status == IdeaStatus.ENVIADA }
                    val total = linkedIdeas.size

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFEEF2F6))
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = "Ideias por status",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = AguiaColors.NavyDark
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                // Donut Chart Canvas
                                Box(
                                    modifier = Modifier.size(130.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    val slices = listOf(
                                        inAnalysis.toFloat() to Color(0xFF3B82F6),
                                        approved.toFloat() to Color(0xFF10B981),
                                        rejected.toFloat() to Color(0xFFEF4444),
                                        sent.toFloat() to Color(0xFF93C5FD)
                                    )

                                    Canvas(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                                        val totalSum = if (total > 0) total.toFloat() else 1f
                                        var startAngle = -90f
                                        val stroke = Stroke(width = 20.dp.toPx(), cap = StrokeCap.Round)

                                        if (total == 0) {
                                            drawArc(
                                                color = Color(0xFFE2E8F0),
                                                startAngle = 0f,
                                                sweepAngle = 360f,
                                                useCenter = false,
                                                style = stroke
                                            )
                                        } else {
                                            slices.forEach { (count, color) ->
                                                if (count > 0) {
                                                    val sweep = (count / totalSum) * 360f
                                                    drawArc(
                                                        color = color,
                                                        startAngle = startAngle,
                                                        sweepAngle = sweep - 4f,
                                                        useCenter = false,
                                                        style = stroke
                                                    )
                                                    startAngle += sweep
                                                }
                                            }
                                        }
                                    }

                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = total.toString(),
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AguiaColors.NavyDark
                                        )
                                        Text(
                                            text = if (total == 1) "Ideia" else "Ideias",
                                            fontSize = 11.sp,
                                            color = AguiaColors.TextSecondary
                                        )
                                    }
                                }

                                // Legend
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    DonutLegendRow(Color(0xFF3B82F6), inAnalysis, "Em análise")
                                    DonutLegendRow(Color(0xFF10B981), approved, "Aprovadas")
                                    DonutLegendRow(Color(0xFFEF4444), rejected, "Rejeitada")
                                    DonutLegendRow(Color(0xFF93C5FD), sent, "Enviadas")
                                }
                            }
                        }
                    }
                }

                // Section Title: Ideias vinculadas
                item {
                    Text(
                        text = "Ideias vinculadas",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AguiaColors.NavyDark
                    )
                }

                // Linked Ideas List or Empty State
                if (linkedIdeas.isEmpty()) {
                    item {
                        AguiaEmptyState(
                            message = "Esta estratégia ainda não possui ideias vinculadas."
                        )
                    }
                } else {
                    items(linkedIdeas, key = { it.id }) { idea ->
                        LinkedIdeaCardItem(
                            idea = idea,
                            onClick = { onIdeaClick(idea.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ConsolidatedMetricCol(
    label: String,
    value: String,
    valueColor: Color
) {
    Column {
        Text(
            text = label,
            fontSize = 11.sp,
            color = AguiaColors.TextSecondary
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
    }
}

@Composable
private fun DonutLegendRow(
    color: Color,
    count: Int,
    label: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = count.toString(),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = AguiaColors.NavyDark
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = AguiaColors.TextSecondary
        )
    }
}

@Composable
private fun LinkedIdeaCardItem(
    idea: Idea,
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
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = idea.title,
                    fontSize = 16.sp,
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
                fontSize = 12.sp,
                color = AguiaColors.TextSecondary
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Enviada por ${idea.authorName}",
                    fontSize = 12.sp,
                    color = AguiaColors.TextSecondary
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onClick() }
                ) {
                    Text(
                        text = "Ver ideia",
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

private fun parseCurrencyNum(value: String?): Double {
    if (value.isNullOrBlank()) return 0.0
    val clean = value.replace("[^0-9,]".toRegex(), "").replace(",", ".")
    return clean.toDoubleOrNull() ?: 0.0
}

private fun formatMonetaryVal(value: Double): String {
    return when {
        value >= 1_000_000 -> String.format(java.util.Locale.US, "R$ %.2f mi", value / 1_000_000).replace(".", ",")
        value >= 1_000 -> String.format(java.util.Locale.US, "R$ %.0f mil", value / 1_000)
        else -> String.format(java.util.Locale.US, "R$ %.2f", value).replace(".", ",")
    }
}
