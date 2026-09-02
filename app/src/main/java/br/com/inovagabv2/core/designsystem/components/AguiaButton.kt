package br.com.inovagabv2.core.designsystem.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.inovagabv2.core.designsystem.AguiaColors

@Composable
fun AguiaButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    outline: Boolean = false,
    containerColor: androidx.compose.ui.graphics.Color = AguiaColors.PrimaryBlue
) {
    if (outline) {
        androidx.compose.material3.OutlinedButton(
            onClick = onClick,
            modifier = modifier
                .fillMaxWidth()
                .height(52.dp),
            enabled = enabled && !isLoading,
            shape = RoundedCornerShape(8.dp),
            colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                contentColor = containerColor
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, containerColor)
        ) {
            Text(text = if (isLoading) "Carregando..." else text)
        }
    } else {
        Button(
            onClick = onClick,
            modifier = modifier
                .fillMaxWidth()
                .height(52.dp),
            enabled = enabled && !isLoading,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = containerColor,
                contentColor = AguiaColors.CardWhite,
                disabledContainerColor = AguiaColors.TextSecondary,
                disabledContentColor = AguiaColors.CardWhite
            )
        ) {
            Text(text = if (isLoading) "Carregando..." else text)
        }
    }
}
