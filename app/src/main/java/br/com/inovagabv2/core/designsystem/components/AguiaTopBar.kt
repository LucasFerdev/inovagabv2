package br.com.inovagabv2.core.designsystem.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.inovagabv2.core.designsystem.AguiaColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AguiaTopBar(
    title: String? = null,
    onBackClick: (() -> Unit)? = null,
    roleTag: String? = null, // e.g. "GESTOR", "LIDERANÇA"
    showLeadershipTag: Boolean = false,
    backgroundColor: Color = Color.Transparent,
    contentColor: Color = AguiaColors.TextPrimary,
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = {
            if (title != null) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
            } else {
                AguiaLogo()
            }
        },
        navigationIcon = {
            if (onBackClick != null) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Voltar",
                        tint = contentColor
                    )
                }
            }
        },
        actions = {
            val tagText = roleTag ?: if (showLeadershipTag) "LIDERANÇA" else null
            if (tagText != null) {
                Text(
                    text = tagText,
                    color = AguiaColors.PrimaryBlue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    letterSpacing = 0.5.sp,
                    modifier = Modifier.padding(end = 16.dp)
                )
            }
            actions()
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = backgroundColor,
            titleContentColor = contentColor
        )
    )
}
