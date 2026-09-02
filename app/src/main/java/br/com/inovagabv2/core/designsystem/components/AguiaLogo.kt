package br.com.inovagabv2.core.designsystem.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import br.com.inovagabv2.R

@Composable
fun AguiaLogo(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.aguia_logo),
        contentDescription = "Viação Águia Branca",
        modifier = modifier
            .fillMaxWidth(0.55f)
            .heightIn(max = 56.dp),
        contentScale = ContentScale.Fit
    )
}
