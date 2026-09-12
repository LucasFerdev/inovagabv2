package br.com.inovagabv2.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.TrackChanges
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.inovagabv2.core.designsystem.AguiaColors
import br.com.inovagabv2.core.navigation.Screen
import br.com.inovagabv2.domain.model.Role

data class NavigationItem(
    val screen: Screen,
    val icon: ImageVector,
    val label: String
)

@Composable
fun AguiaBottomBar(
    currentRoute: String?,
    role: Role,
    onNavigate: (String) -> Unit
) {
    val items = when (role) {
        Role.OPERADOR -> listOf(
            NavigationItem(Screen.OperatorHome, Icons.Default.Home, "Início"),
            NavigationItem(Screen.MyIdeas, Icons.Outlined.Lightbulb, "Minhas ideias"),
            NavigationItem(Screen.OperatorStrategy, Icons.Outlined.TrackChanges, "Estratégias"),
            NavigationItem(Screen.Profile, Icons.Outlined.Person, "Perfil")
        )
        Role.GESTOR -> listOf(
            NavigationItem(Screen.ManagerHome, Icons.Default.Home, "Início"),
            NavigationItem(Screen.ManagerIdeas, Icons.Outlined.Lightbulb, "Ideias"),
            NavigationItem(Screen.ManagerProjects, Icons.Default.Work, "Projetos"),
            NavigationItem(Screen.OperatorStrategy, Icons.AutoMirrored.Filled.Assignment, "Estratégia"),
            NavigationItem(Screen.Profile, Icons.Outlined.Person, "Perfil")
        )
        Role.LIDERANCA -> listOf(
            NavigationItem(Screen.LeadershipDashboard, Icons.Default.Home, "Início"),
            NavigationItem(Screen.LeadershipStrategy, Icons.Outlined.TrackChanges, "Estratégia"),
            NavigationItem(Screen.LeadershipProjects, Icons.Default.Folder, "Projetos"),
            NavigationItem(Screen.LeadershipResults, Icons.Default.BarChart, "Resultados"),
            NavigationItem(Screen.Profile, Icons.Outlined.Person, "Perfil")
        )
    }

    Surface(
        color = Color.White,
        tonalElevation = 8.dp,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val isSelected = currentRoute == item.screen.route
                val contentColor = if (isSelected) AguiaColors.PrimaryBlue else AguiaColors.TextSecondary

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    IconButton(
                        onClick = { onNavigate(item.screen.route) },
                        modifier = Modifier.size(38.dp)
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            tint = contentColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Text(
                        text = item.label,
                        color = contentColor,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .width(28.dp)
                            .height(3.dp)
                            .background(
                                if (isSelected) AguiaColors.PrimaryBlue else Color.Transparent
                            )
                    )
                }
            }
        }
    }
}
