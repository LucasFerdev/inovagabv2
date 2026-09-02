package br.com.inovagabv2.presentation.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.inovagabv2.R
import br.com.inovagabv2.core.designsystem.AguiaColors
import br.com.inovagabv2.core.designsystem.components.AguiaBottomBar
import br.com.inovagabv2.core.designsystem.components.AguiaTopBar
import br.com.inovagabv2.core.navigation.Screen
import br.com.inovagabv2.domain.model.Role
import coil3.compose.AsyncImage

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onLogout: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToSugestoes: () -> Unit,
    onNavigateToCommunications: () -> Unit
) {
    val userState by viewModel.user.collectAsState()
    val profileImageUri by viewModel.profileImageUri.collectAsState()
    val scrollState = rememberScrollState()

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> viewModel.updateProfileImage(uri) }
    )

    Scaffold(
        topBar = {
            AguiaTopBar(title = "Perfil")
        },
        bottomBar = {
            userState?.let { sessionUser ->
                AguiaBottomBar(
                    currentRoute = Screen.Profile.route,
                    role = sessionUser.role,
                    onNavigate = { route ->
                        when (route) {
                            Screen.OperatorHome.route, Screen.ManagerHome.route, Screen.LeadershipDashboard.route -> onNavigateToHome()
                            Screen.MyIdeas.route, Screen.ManagerIdeas.route -> onNavigateToSugestoes()
                            Screen.OperatorCommunications.route, Screen.ManagerProjects.route, Screen.LeadershipProjects.route -> onNavigateToCommunications()
                            Screen.Profile.route -> { /* Already here */ }
                            else -> {}
                        }
                    }
                )
            }
        },
        containerColor = AguiaColors.Background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(AguiaColors.NavyDark.copy(alpha = 0.1f))
                    .border(2.dp, AguiaColors.PrimaryBlue, CircleShape)
                    .clickable {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                if (profileImageUri != null) {
                    AsyncImage(
                        model = profileImageUri,
                        contentDescription = "Foto de perfil",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = AguiaColors.TextSecondary
                    )
                }
                
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(4.dp)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(AguiaColors.PrimaryBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Alterar foto",
                        tint = AguiaColors.CardWhite,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = userState?.name ?: "",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = AguiaColors.TextPrimary
            )
            
            Text(
                text = userState?.role?.name?.lowercase()?.replaceFirstChar { it.uppercase() } ?: "",
                style = MaterialTheme.typography.bodyLarge,
                color = AguiaColors.PrimaryBlue,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = userState?.email ?: "",
                style = MaterialTheme.typography.bodyMedium,
                color = AguiaColors.TextSecondary
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Meu Nível Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Meu nível",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = AguiaColors.PrimaryBlue,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = userState?.role?.name?.lowercase()?.replaceFirstChar { it.uppercase() } ?: "",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (userState?.role == Role.OPERADOR) "Você envia sugestões e acompanha o andamento." else "Você analisa e toma decisões.",
                                style = MaterialTheme.typography.bodySmall,
                                color = AguiaColors.TextSecondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sobre os níveis Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Sobre os níveis",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    LevelItem(Role.OPERADOR, "Envia sugestões e acompanha seu andamento.")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                    LevelItem(Role.GESTOR, "Analisa sugestões da equipe e toma decisões.")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                    LevelItem(Role.LIDERANCA, "Avalia e aprova sugestões estratégicas.")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { viewModel.logout(onLogout) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AguiaColors.ErrorRed.copy(alpha = 0.1f),
                    contentColor = AguiaColors.ErrorRed
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = null
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sair da conta", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun LevelItem(role: Role, description: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = when(role) {
                Role.OPERADOR -> Icons.Default.Person
                Role.GESTOR -> Icons.Default.Person
                Role.LIDERANCA -> Icons.Default.Work
            },
            contentDescription = null,
            tint = when(role) {
                Role.OPERADOR -> AguiaColors.PrimaryBlue
                Role.GESTOR -> AguiaColors.ManagerPurple
                Role.LIDERANCA -> AguiaColors.SuccessGreen
            },
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = role.name.lowercase().replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = AguiaColors.TextSecondary
            )
        }
    }
}
