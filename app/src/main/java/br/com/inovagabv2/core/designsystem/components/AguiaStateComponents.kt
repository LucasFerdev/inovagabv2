package br.com.inovagabv2.core.designsystem.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.inovagabv2.core.designsystem.AguiaColors

@Composable
fun AguiaLoadingState(
    modifier: Modifier = Modifier,
    message: String = "Carregando..."
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            color = AguiaColors.PrimaryBlue,
            modifier = Modifier.size(36.dp),
            strokeWidth = 3.dp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            color = AguiaColors.TextSecondary,
            fontSize = 14.sp
        )
    }
}

@Composable
fun AguiaEmptyState(
    message: String,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            color = AguiaColors.TextSecondary,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )
        if (actionText != null && onActionClick != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onActionClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AguiaColors.PrimaryBlue,
                    contentColor = Color.White
                )
            ) {
                Text(text = actionText, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun AguiaErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            color = AguiaColors.ErrorRed,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(
            onClick = onRetry,
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = AguiaColors.PrimaryBlue
            )
        ) {
            Text(text = "Tentar novamente", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        }
    }
}
