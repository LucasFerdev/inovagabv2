package br.com.inovagabv2.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.inovagabv2.core.designsystem.AguiaColors
import br.com.inovagabv2.core.util.UserUtils

@Composable
fun AguiaUserAvatar(
    name: String?,
    modifier: Modifier = Modifier,
    size: Dp = 36.dp,
    backgroundColor: Color = AguiaColors.PrimaryBlue.copy(alpha = 0.15f),
    textColor: Color = AguiaColors.NavyDark
) {
    val initials = UserUtils.calculateInitials(name)
    val fontSize = (size.value * 0.38f).sp

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            color = textColor,
            fontWeight = FontWeight.Bold,
            fontSize = fontSize
        )
    }
}
