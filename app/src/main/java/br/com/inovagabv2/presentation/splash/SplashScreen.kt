package br.com.inovagabv2.presentation.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import br.com.inovagabv2.R
import br.com.inovagabv2.core.session.AppInitState
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
fun SplashScreen(
    appInitState: AppInitState = AppInitState.Loading,
    onNavigate: (String) -> Unit
) {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current

    val screenWidthPx = with(density) {
        configuration.screenWidthDp.dp.toPx()
    }

    val offsetX = remember {
        Animatable(0f)
    }

    var minSplashFinished by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        // Tempo em que o logo permanece parado no centro
        delay(1_200)

        offsetX.animateTo(
            targetValue = -screenWidthPx,
            animationSpec = tween(
                durationMillis = 1_800,
                easing = FastOutSlowInEasing
            )
        )

        minSplashFinished = true
    }

    LaunchedEffect(minSplashFinished, appInitState) {
        if (minSplashFinished) {
            val destination = SplashNavigationResolver.resolveDestination(appInitState)
            if (destination != null) {
                onNavigate(destination)
            }
        }
    }

    SplashContent(
        offsetX = offsetX.value
    )
}

@Composable
private fun SplashContent(
    offsetX: Float
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(
                id = R.drawable.aguia_logo
            ),
            contentDescription = "Viação Águia Branca",
            modifier = Modifier
                .fillMaxWidth(0.99f)
                .heightIn(max = 250.dp)
                .scale(1.17f)
                .offset {
                    IntOffset(
                        x = offsetX.roundToInt(),
                        y = 0
                    )
                },
            contentScale = ContentScale.Fit
        )
    }
}

@Preview(
    name = "Splash Screen",
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun SplashScreenPreview() {
    SplashContent(
        offsetX = 0f
    )
}
