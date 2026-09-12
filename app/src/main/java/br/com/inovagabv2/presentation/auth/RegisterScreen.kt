package br.com.inovagabv2.presentation.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.inovagabv2.R
import br.com.inovagabv2.core.designsystem.AguiaColors
import br.com.inovagabv2.domain.model.Role

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel,
    onRegisterSuccess: (Role) -> Unit,
    onBackClick: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var companyUnit by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var acceptedTerms by remember { mutableStateOf(false) }

    var showAccessCodeField by remember { mutableStateOf(false) }
    var accessCode by remember { mutableStateOf("") }
    var accessCodeVisible by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onRegisterSuccess(uiState.createdUser?.role ?: Role.OPERADOR)
        }
    }

    Scaffold(
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header: Back Arrow (left) & Logo + INOVAGAB (center)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 20.dp)
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Voltar",
                        tint = AguiaColors.NavyDark
                    )
                }

                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.aguia_logo),
                        contentDescription = "Viação Águia Branca",
                        modifier = Modifier.height(32.dp),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "INOVAGAB",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AguiaColors.PrimaryBlue,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Title Section
            Text(
                text = "Crie sua conta",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = AguiaColors.NavyDark,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Cadastre-se para compartilhar ideias e transformar o futuro.",
                style = MaterialTheme.typography.bodyMedium,
                color = AguiaColors.TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            // 1. Nome completo
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                placeholder = { Text("Nome completo", color = AguiaColors.TextSecondary, fontSize = 15.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = null,
                        tint = AguiaColors.NavyDark
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = AguiaColors.PrimaryBlue,
                    unfocusedBorderColor = AguiaColors.TextSecondary.copy(alpha = 0.25f)
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 2. E-mail corporativo
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("E-mail corporativo", color = AguiaColors.TextSecondary, fontSize = 15.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Mail,
                        contentDescription = null,
                        tint = AguiaColors.NavyDark
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = AguiaColors.PrimaryBlue,
                    unfocusedBorderColor = AguiaColors.TextSecondary.copy(alpha = 0.25f)
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 3. Empresa ou unidade
            OutlinedTextField(
                value = companyUnit,
                onValueChange = { companyUnit = it },
                placeholder = { Text("Empresa ou unidade", color = AguiaColors.TextSecondary, fontSize = 15.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Business,
                        contentDescription = null,
                        tint = AguiaColors.NavyDark
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = AguiaColors.PrimaryBlue,
                    unfocusedBorderColor = AguiaColors.TextSecondary.copy(alpha = 0.25f)
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 4. Senha
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Senha", color = AguiaColors.TextSecondary, fontSize = 15.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Lock,
                        contentDescription = null,
                        tint = AguiaColors.NavyDark
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Mostrar/Ocultar senha",
                            tint = AguiaColors.NavyDark
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = AguiaColors.PrimaryBlue,
                    unfocusedBorderColor = AguiaColors.TextSecondary.copy(alpha = 0.25f)
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 5. Confirmar senha
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                placeholder = { Text("Confirmar senha", color = AguiaColors.TextSecondary, fontSize = 15.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Lock,
                        contentDescription = null,
                        tint = AguiaColors.NavyDark
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Mostrar/Ocultar senha",
                            tint = AguiaColors.NavyDark
                        )
                    }
                },
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = AguiaColors.PrimaryBlue,
                    unfocusedBorderColor = AguiaColors.TextSecondary.copy(alpha = 0.25f)
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Option: Possui um código de acesso? Adicionar / Remover
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Possui um código de acesso? ",
                    fontSize = 14.sp,
                    color = AguiaColors.NavyDark
                )
                Text(
                    text = if (showAccessCodeField) "Remover" else "Adicionar",
                    fontSize = 14.sp,
                    color = AguiaColors.PrimaryBlue,
                    fontWeight = FontWeight.SemiBold,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable {
                        showAccessCodeField = !showAccessCodeField
                        if (!showAccessCodeField) {
                            accessCode = ""
                            accessCodeVisible = false
                        }
                    }
                )
            }

            if (showAccessCodeField) {
                Spacer(modifier = Modifier.height(12.dp))

                // OutlinedTextField for Código de acesso (opcional)
                OutlinedTextField(
                    value = accessCode,
                    onValueChange = { accessCode = it },
                    placeholder = { Text("Código de acesso (opcional)", color = AguiaColors.TextSecondary, fontSize = 15.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Key,
                            contentDescription = null,
                            tint = AguiaColors.NavyDark
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { accessCodeVisible = !accessCodeVisible }) {
                            Icon(
                                imageVector = if (accessCodeVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "Mostrar/Ocultar código",
                                tint = AguiaColors.NavyDark
                            )
                        }
                    },
                    visualTransformation = if (accessCodeVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = AguiaColors.PrimaryBlue,
                        unfocusedBorderColor = AguiaColors.TextSecondary.copy(alpha = 0.25f)
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Fornecido pela administração para Gestão ou Liderança.",
                    fontSize = 12.sp,
                    color = AguiaColors.TextSecondary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp)
                )
            }

            if (uiState.error != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = uiState.error ?: "",
                    color = AguiaColors.ErrorRed,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.Start)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Terms Checkbox Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { acceptedTerms = !acceptedTerms },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = acceptedTerms,
                    onCheckedChange = { acceptedTerms = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = AguiaColors.PrimaryBlue,
                        uncheckedColor = AguiaColors.TextSecondary
                    )
                )

                Text(
                    text = "Li e aceito os ",
                    fontSize = 13.sp,
                    color = AguiaColors.NavyDark
                )
                Text(
                    text = "Termos de Uso",
                    fontSize = 13.sp,
                    color = AguiaColors.PrimaryBlue,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = " e a ",
                    fontSize = 13.sp,
                    color = AguiaColors.NavyDark
                )
                Text(
                    text = "Política de Privacidade",
                    fontSize = 13.sp,
                    color = AguiaColors.PrimaryBlue,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Primary Button: Criar conta
            Button(
                onClick = {
                    val finalAccessCode = if (showAccessCodeField && accessCode.isNotBlank()) accessCode else null
                    viewModel.register(
                        fullName = fullName,
                        email = email,
                        companyUnit = companyUnit,
                        password = password,
                        confirmPassword = confirmPassword,
                        acceptedTerms = acceptedTerms,
                        accessCode = finalAccessCode
                    )
                },
                enabled = !uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AguiaColors.PrimaryBlue,
                    contentColor = Color.White
                )
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                } else {
                    Text(
                        text = "Criar conta",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sign In link: Já possui uma conta? Acessar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Já possui uma conta? ",
                    color = AguiaColors.NavyDark,
                    fontSize = 14.sp
                )
                Text(
                    text = "Acessar",
                    color = AguiaColors.PrimaryBlue,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable { onNavigateToLogin() }
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Security Badge: Seus dados estão protegidos
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Shield,
                    contentDescription = null,
                    tint = AguiaColors.TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Seus dados estão protegidos",
                    fontSize = 12.sp,
                    color = AguiaColors.TextSecondary
                )
            }
        }
    }
}
