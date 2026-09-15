package br.com.inovagabv2.presentation.profile

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.HeadsetMic
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.inovagabv2.BuildConfig
import br.com.inovagabv2.R
import br.com.inovagabv2.core.designsystem.AguiaColors
import br.com.inovagabv2.core.designsystem.components.AguiaBottomBar
import br.com.inovagabv2.core.designsystem.components.AguiaTopBar
import br.com.inovagabv2.core.designsystem.components.AguiaUserAvatar
import br.com.inovagabv2.core.navigation.Screen
import br.com.inovagabv2.core.util.UserUtils

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onLogout: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToSugestoes: () -> Unit,
    onNavigateToCommunications: () -> Unit,
    onNavigateToRanking: () -> Unit = {}
) {
    val user by viewModel.user.collectAsState()
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val supportEmail = stringResource(R.string.support_email)

    fun openSupportEmail() {
        try {
            val subject = "Suporte InovaGAB"
            val body = """
                Olá, preciso de ajuda com o InovaGAB.

                Nome: ${user?.name ?: "Não informado"}
                E-mail: ${user?.email ?: "Não informado"}
                Empresa: ${user?.company ?: "Viação Águia Branca"}
                Perfil: ${UserUtils.formatRoleName(user?.role)}

                Descrição do problema:
            """.trimIndent()

            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:$supportEmail")
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, body)
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            // Friendly fallback if no email app installed
        }
    }

    Scaffold(
        topBar = {
            AguiaTopBar(
                userName = user?.name,
                role = user?.role,
                showRoleBadge = true
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            user?.let { sessionUser ->
                AguiaBottomBar(
                    currentRoute = Screen.Profile.route,
                    role = sessionUser.role,
                    onNavigate = { route ->
                        when (route) {
                            Screen.OperatorHome.route, Screen.ManagerHome.route, Screen.LeadershipDashboard.route -> onNavigateToHome()
                            Screen.MyIdeas.route, Screen.ManagerIdeas.route -> onNavigateToSugestoes()
                            Screen.OperatorCommunications.route, Screen.ManagerProjects.route, Screen.LeadershipProjects.route, Screen.OperatorStrategy.route, Screen.ManagerStrategy.route, Screen.LeadershipStrategy.route -> onNavigateToCommunications()
                            Screen.Profile.route -> { /* Already here */ }
                            else -> {}
                        }
                    }
                )
            }
        },
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Page Title
            Text(
                text = "Perfil",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = AguiaColors.NavyDark,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Card 1: User Profile Header Card (Light blue gradient)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFFEBF3FE),
                                    Color(0xFFE2EDFE)
                                )
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(vertical = 24.dp, horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AguiaUserAvatar(
                            name = user?.name,
                            size = 80.dp,
                            backgroundColor = Color(0xFFC7DCFC),
                            textColor = AguiaColors.NavyDark
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = user?.name ?: "Usuário",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = AguiaColors.NavyDark
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = user?.email ?: "",
                            fontSize = 14.sp,
                            color = AguiaColors.TextSecondary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Pill badge: CONTA ATIVA
                        val isActive = user?.active != false
                        val activeBg = if (isActive) Color(0xFFDCFCE7) else Color(0xFFFEE2E2)
                        val activeText = if (isActive) Color(0xFF16A34A) else Color(0xFFDC2626)
                        val activeLabel = if (isActive) "CONTA ATIVA" else "CONTA INATIVA"

                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = activeBg
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.CheckCircle,
                                    contentDescription = null,
                                    tint = activeText,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = activeLabel,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = activeText,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Card 2: Informações da conta
            Text(
                text = "Informações da conta",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = AguiaColors.NavyDark,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFEEF2F6))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Item 1: Empresa
                    ProfileInfoRow(
                        icon = Icons.Outlined.Business,
                        label = "Empresa",
                        value = user?.company ?: "Viação Águia Branca"
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = Color(0xFFF1F5F9)
                    )

                    // Item 2: Perfil de acesso
                    ProfileInfoRow(
                        icon = Icons.Outlined.Person,
                        label = "Perfil de acesso",
                        value = UserUtils.formatRoleName(user?.role)
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = Color(0xFFF1F5F9)
                    )

                    // Item 3: Membro desde
                    ProfileInfoRow(
                        icon = Icons.Outlined.CalendarToday,
                        label = "Membro desde",
                        value = user?.createdAt ?: "10 set 2026"
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = Color(0xFFF1F5F9)
                    )

                    // Item 4: Segurança
                    ProfileInfoRow(
                        icon = Icons.Outlined.VerifiedUser,
                        label = "Segurança",
                        value = "Sessão protegida"
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = Color(0xFFF1F5F9)
                    )

                    // Item 5: Ranking de inovação (BEFORE Support!)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToRanking() }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFEFF6FF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.EmojiEvents,
                                contentDescription = null,
                                tint = AguiaColors.PrimaryBlue,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Ranking de inovação",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = AguiaColors.NavyDark
                            )
                            Text(
                                text = "Veja os colaboradores que transformaram ideias em resultados",
                                fontSize = 12.sp,
                                color = AguiaColors.TextSecondary
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = AguiaColors.TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = Color(0xFFF1F5F9)
                    )

                    // Item 6: Falar com o suporte
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { openSupportEmail() }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFEFF6FF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.HeadsetMic,
                                contentDescription = null,
                                tint = AguiaColors.PrimaryBlue,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Falar com o suporte",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = AguiaColors.NavyDark
                            )
                            Text(
                                text = "Registre uma solicitação de atendimento.",
                                fontSize = 12.sp,
                                color = AguiaColors.TextSecondary
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = AguiaColors.TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card 3: Seus dados estão protegidos
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                border = BorderStroke(1.dp, Color(0xFFDBEAFE))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(AguiaColors.PrimaryBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Shield,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = "Seus dados estão protegidos",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = AguiaColors.NavyDark
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "As informações da conta são administradas com segurança pelo InovaGAB.",
                            fontSize = 12.sp,
                            color = AguiaColors.TextSecondary,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Logout Button
            OutlinedButton(
                onClick = { viewModel.logout(onLogout) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.5.dp, Color(0xFFEF4444)),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFFEF4444)
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = null,
                    tint = Color(0xFFEF4444),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Sair da conta",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFFEF4444)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Footer Version
            Text(
                text = "InovaGAB • Versão ${BuildConfig.VERSION_NAME}",
                fontSize = 12.sp,
                color = AguiaColors.TextSecondary,
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ProfileInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFEFF6FF)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = AguiaColors.PrimaryBlue,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column {
            Text(
                text = label,
                fontSize = 12.sp,
                color = AguiaColors.TextSecondary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = AguiaColors.NavyDark
            )
        }
    }
}
