package br.com.inovagabv2.presentation.ranking

import androidx.compose.foundation.Image
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.inovagabv2.R
import br.com.inovagabv2.core.designsystem.AguiaColors
import br.com.inovagabv2.core.designsystem.components.*
import br.com.inovagabv2.core.util.UserUtils
import br.com.inovagabv2.domain.model.RankingColaborador
import br.com.inovagabv2.domain.model.Role

@Composable
fun InnovationRankingScreen(
    onBackClick: () -> Unit,
    viewModel: InnovationRankingViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val user by viewModel.user.collectAsState()

    val currentRole = user?.role ?: Role.OPERADOR

    // Criteria Dialog
    if (state.showCriteriaDialog) {
        AlertDialog(
            onDismissRequest = viewModel::onDismissCriteriaDialog,
            title = { Text("Critérios do Ranking", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    text = "O ranking considera operadores com ideias aprovadas que originaram projetos concluídos. Cada ideia é contabilizada apenas uma vez.",
                    fontSize = 14.sp,
                    color = AguiaColors.TextSecondary,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                TextButton(onClick = viewModel::onDismissCriteriaDialog) {
                    Text("Entendi", fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            AguiaTopBar(
                userName = user?.name,
                role = currentRole,
                onBackClick = onBackClick
            )
        },
        containerColor = Color.White
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                AguiaLoadingState(message = "Carregando ranking de inovação...")
            }
        } else if (state.error != null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                AguiaErrorState(
                    message = state.error ?: "Erro ao carregar o ranking.",
                    onRetry = viewModel::loadRanking
                )
            }
        } else if (state.ranking.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                AguiaEmptyState(
                    message = "Ainda não existem ideias implementadas para formar o ranking."
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header Titles
                item {
                    Column {
                        Text(
                            text = "Ranking de inovação",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = AguiaColors.NavyDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Reconhecendo ideias que viraram resultados.",
                            fontSize = 14.sp,
                            color = AguiaColors.TextSecondary
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Ranking geral",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = AguiaColors.PrimaryBlue
                        )
                    }
                }

                // Banner Podium Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                        ) {
                            // Background Image
                            Image(
                                painter = painterResource(id = R.drawable.banner_rank),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )

                            // Podium Content Overlay
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                // 2nd Place (Left)
                                PodiumSpot(
                                    colaborador = state.top2,
                                    defaultPos = "2º",
                                    badgeColor = Color(0xFF94A3B8),
                                    modifier = Modifier.weight(1f)
                                )

                                // 1st Place (Center - Highest)
                                PodiumSpot(
                                    colaborador = state.top1,
                                    defaultPos = "1º",
                                    badgeColor = Color(0xFFEAB308),
                                    modifier = Modifier.weight(1f)
                                )

                                // 3rd Place (Right)
                                PodiumSpot(
                                    colaborador = state.top3,
                                    defaultPos = "3º",
                                    badgeColor = Color(0xFFD97706),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                // Classification Header + "Ver critérios"
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Classificação geral",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = AguiaColors.NavyDark
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { viewModel.onShowCriteriaDialog() }
                        ) {
                            Text(
                                text = "Ver critérios",
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

                // General Classification List (4th onwards)
                if (state.restOfRanking.isEmpty()) {
                    item {
                        Text(
                            text = "Todos os participantes classificados estão nos destaques do topo.",
                            fontSize = 13.sp,
                            color = AguiaColors.TextSecondary,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                } else {
                    items(state.restOfRanking, key = { it.posicao }) { colab ->
                        RankingItemRow(colaborador = colab)
                    }
                }
            }
        }
    }
}

@Composable
private fun PodiumSpot(
    colaborador: RankingColaborador?,
    defaultPos: String,
    badgeColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(bottom = 6.dp)
    ) {
        if (colaborador != null) {
            val initials = UserUtils.calculateInitials(colaborador.nome)
            val implText = if (colaborador.ideiasImplementadas == 1L) "1 implementada" else "${colaborador.ideiasImplementadas} implementadas"

            Box(contentAlignment = Alignment.BottomCenter) {
                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    shadowElevation = 2.dp,
                    modifier = Modifier.size(46.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = initials,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = AguiaColors.NavyDark
                        )
                    }
                }

                Surface(
                    shape = CircleShape,
                    color = badgeColor,
                    modifier = Modifier
                        .offset(y = 8.dp)
                        .size(18.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "${colaborador.posicao}º",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = colaborador.nome,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = AguiaColors.NavyDark,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )

            Text(
                text = implText,
                fontSize = 10.sp,
                color = AguiaColors.TextSecondary,
                maxLines = 1,
                textAlign = TextAlign.Center
            )
        } else {
            // Empty Base
            Surface(
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.5f),
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = defaultPos,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
private fun RankingItemRow(colaborador: RankingColaborador) {
    val initials = UserUtils.calculateInitials(colaborador.nome)
    val ideasText = if (colaborador.ideiasImplementadas == 1L) "1 ideia" else "${colaborador.ideiasImplementadas} ideias"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFEEF2F6))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${colaborador.posicao}º",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = AguiaColors.NavyDark,
                modifier = Modifier.width(32.dp)
            )

            Surface(
                shape = CircleShape,
                color = Color(0xFFEFF6FF),
                modifier = Modifier.size(38.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = initials,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = AguiaColors.PrimaryBlue
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = colaborador.nome,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = AguiaColors.NavyDark
                )
                if (colaborador.empresa.isNotBlank()) {
                    Text(
                        text = colaborador.empresa,
                        fontSize = 12.sp,
                        color = AguiaColors.TextSecondary
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Lightbulb,
                    contentDescription = null,
                    tint = AguiaColors.PrimaryBlue,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = ideasText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = AguiaColors.PrimaryBlue
                )
            }
        }
    }
}
