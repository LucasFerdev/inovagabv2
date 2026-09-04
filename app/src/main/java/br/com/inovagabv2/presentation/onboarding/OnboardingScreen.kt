package br.com.inovagabv2.presentation.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.inovagabv2.R
import br.com.inovagabv2.core.designsystem.AguiaColors
import kotlinx.coroutines.launch

private data class OnboardingPage(
    val titleLine1: String,
    val titleLine2: String,
    val titleLine3: String? = null,
    val subtitle: String,
    val imageRes: Int,
    val isImageBottom: Boolean = false
)

@Composable
fun OnboardingScreen(
    onFinish: () -> Unit
) {
    val pages = listOf(
        OnboardingPage(
            titleLine1 = "Sua ideia",
            titleLine2 = "pode",
            titleLine3 = "transformar.",
            subtitle = "Compartilhe sugestões e ajude a construir o futuro do Grupo Águia Branca.",
            imageRes = R.drawable.onbord_tela1,
            isImageBottom = true
        ),
        OnboardingPage(
            titleLine1 = "Acompanhe",
            titleLine2 = "cada etapa.",
            subtitle = "Veja a evolução da sua sugestão, da análise até a implementação.",
            imageRes = R.drawable.onbord_tela2,
            isImageBottom = false
        ),
        OnboardingPage(
            titleLine1 = "Inovação que",
            titleLine2 = "gera resultados.",
            subtitle = "Conectamos pessoas, projetos e resultados em um só lugar.",
            imageRes = R.drawable.onbord_tela3,
            isImageBottom = false
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Logo Area
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 12.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Image(
                    painter = painterResource(id = R.drawable.aguia_logo),
                    contentDescription = "Viação Águia Branca",
                    modifier = Modifier
                        .height(32.dp),
                    contentScale = ContentScale.Fit,
                    alignment = Alignment.CenterStart
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "INOVAGAB",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = AguiaColors.PrimaryBlue,
                    letterSpacing = 0.5.sp
                )
            }

            // Pager Content Area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { pageIndex ->
                    OnboardingPageContent(page = pages[pageIndex])
                }
            }

            // Bottom Navigation & Controls
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Page Indicator Dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    repeat(pages.size) { index ->
                        val isSelected = pagerState.currentPage == index
                        Box(
                            modifier = Modifier
                                .size(if (isSelected) 8.dp else 7.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) AguiaColors.PrimaryBlue
                                    else AguiaColors.PrimaryBlue.copy(alpha = 0.3f)
                                )
                        )
                    }
                }

                // Primary Button: Começar ->
                Button(
                    onClick = {
                        if (pagerState.currentPage < pages.size - 1) {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        } else {
                            onFinish()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AguiaColors.PrimaryBlue,
                        contentColor = Color.White
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Começar",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Secondary Link: Acessar
                TextButton(
                    onClick = onFinish,
                    modifier = Modifier.height(36.dp)
                ) {
                    Text(
                        text = "Acessar",
                        color = AguiaColors.PrimaryBlue,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        textDecoration = TextDecoration.Underline
                    )
                }
            }
        }
    }
}

@Composable
private fun OnboardingPageContent(
    page: OnboardingPage
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.Start
    ) {
        if (page.isImageBottom) {
            // Page 1: Text Top, Image Bottom
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text(
                    text = page.titleLine1,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = AguiaColors.NavyDark,
                    lineHeight = 40.sp
                )
                Text(
                    text = page.titleLine2,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = AguiaColors.NavyDark,
                    lineHeight = 40.sp
                )
                page.titleLine3?.let { line3 ->
                    Text(
                        text = line3,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = AguiaColors.NavyDark,
                        lineHeight = 40.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = page.subtitle,
                    fontSize = 14.sp,
                    color = AguiaColors.TextSecondary,
                    lineHeight = 20.sp,
                    modifier = Modifier.fillMaxWidth(0.9f)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = page.imageRes),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(0.95f),
                    contentScale = ContentScale.Fit
                )
            }
        } else {
            // Pages 2 & 3: Image Top, Text Bottom
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = page.imageRes),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = page.titleLine1,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = AguiaColors.NavyDark,
                    lineHeight = 40.sp
                )
                Text(
                    text = page.titleLine2,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = AguiaColors.NavyDark,
                    lineHeight = 40.sp
                )
                page.titleLine3?.let { line3 ->
                    Text(
                        text = line3,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = AguiaColors.NavyDark,
                        lineHeight = 40.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = page.subtitle,
                    fontSize = 14.sp,
                    color = AguiaColors.TextSecondary,
                    lineHeight = 20.sp,
                    modifier = Modifier.fillMaxWidth(0.9f)
                )
            }
        }
    }
}
