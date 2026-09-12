package br.com.inovagabv2.core.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.inovagabv2.core.designsystem.AguiaColors
import br.com.inovagabv2.core.util.UserUtils
import br.com.inovagabv2.domain.model.Role

@Composable
fun AguiaRoleBadge(
    role: Role?,
    modifier: Modifier = Modifier
) {
    val text = UserUtils.formatRoleBadge(role)

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        border = BorderStroke(1.dp, AguiaColors.PrimaryBlue)
    ) {
        Text(
            text = text,
            color = AguiaColors.PrimaryBlue,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}
