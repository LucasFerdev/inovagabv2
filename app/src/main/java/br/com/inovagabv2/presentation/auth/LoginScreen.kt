package br.com.inovagabv2.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.inovagabv2.core.designsystem.AguiaColors
import br.com.inovagabv2.core.designsystem.components.AguiaButton
import br.com.inovagabv2.core.designsystem.components.AguiaLogo
import br.com.inovagabv2.core.designsystem.components.AguiaTextField
import br.com.inovagabv2.domain.model.Role

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: (Role) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(uiState.successUser) {
        uiState.successUser?.let {
            onLoginSuccess(it.role)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AguiaColors.Background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AguiaLogo()
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Text(
            text = "Bem-vindo",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = AguiaColors.TextPrimary,
            modifier = Modifier.align(Alignment.Start)
        )
        
        Text(
            text = "Faça login para continuar",
            fontSize = 16.sp,
            color = AguiaColors.TextSecondary,
            modifier = Modifier.align(Alignment.Start)
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        AguiaTextField(
            value = email,
            onValueChange = { email = it },
            label = "E-mail",
            error = uiState.error
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        AguiaTextField(
            value = password,
            onValueChange = { password = it },
            label = "Senha",
            isPassword = true
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        AguiaButton(
            text = "Entrar",
            onClick = { viewModel.login(email, password) },
            isLoading = uiState.isLoading
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        TextButton(onClick = { /* Esqueci minha senha */ }) {
            Text(
                text = "Esqueci minha senha",
                color = AguiaColors.PrimaryBlue,
                fontWeight = FontWeight.Medium
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Dica para o usuário mock
        Card(
            colors = CardDefaults.cardColors(containerColor = AguiaColors.NavyDark.copy(alpha = 0.05f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Usuários de teste:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text("Operador: operador@aguia.com", fontSize = 12.sp)
                Text("Gestor: gestor@aguia.com", fontSize = 12.sp)
                Text("Liderança: lideranca@aguia.com", fontSize = 12.sp)
            }
        }
    }
}
