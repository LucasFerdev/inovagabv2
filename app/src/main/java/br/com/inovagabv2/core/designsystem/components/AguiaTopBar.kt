package br.com.inovagabv2.core.designsystem.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.inovagabv2.R
import br.com.inovagabv2.core.designsystem.AguiaColors
import br.com.inovagabv2.domain.model.Role

@Composable
fun AguiaTopBar(
    modifier: Modifier = Modifier,
    userName: String? = null,
    role: Role? = null,
    title: String? = null,
    roleTag: String? = null,
    showLeadershipTag: Boolean = false,
    showRoleBadge: Boolean = true,
    backgroundColor: Color = Color.Transparent,
    contentColor: Color = AguiaColors.NavyDark,
    onBackClick: (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        if (onBackClick != null) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar",
                    tint = contentColor
                )
            }
        }

        // Center / Start Logo or Title
        val logoAlignment = if (onBackClick != null) Alignment.Center else Alignment.CenterStart
        Row(
            modifier = Modifier.align(logoAlignment),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (title != null && onBackClick != null && !title.equals("Perfil", ignoreCase = true)) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.aguia_logo),
                    contentDescription = "Viação Águia Branca",
                    modifier = Modifier.height(28.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }

        // Right side: Role badge & User Avatar
        Row(
            modifier = Modifier.align(Alignment.CenterEnd),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val badgeText = roleTag ?: if (showLeadershipTag) "LIDERANÇA" else null
            if (badgeText != null && onBackClick == null) {
                val parsedRole = when (badgeText.uppercase()) {
                    "GESTOR" -> Role.GESTOR
                    "LIDERANÇA", "LIDERANCA" -> Role.LIDERANCA
                    else -> Role.OPERADOR
                }
                AguiaRoleBadge(role = parsedRole)
            } else if (showRoleBadge && role != null && onBackClick == null) {
                AguiaRoleBadge(role = role)
            }

            AguiaUserAvatar(name = userName, size = 36.dp)
        }
    }
}
