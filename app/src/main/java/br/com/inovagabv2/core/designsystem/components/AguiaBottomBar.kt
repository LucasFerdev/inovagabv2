package br.com.inovagabv2.core.designsystem.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
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
            NavigationItem(Screen.MyIdeas, Icons.Default.Lightbulb, "Sugestões"),
            NavigationItem(Screen.OperatorCommunications, Icons.AutoMirrored.Filled.Chat, "Comunicações"),
            NavigationItem(Screen.Profile, Icons.Default.Person, "Perfil")
        )
        Role.GESTOR -> listOf(
            NavigationItem(Screen.ManagerHome, Icons.Default.Home, "Início"),
            NavigationItem(Screen.ManagerIdeas, Icons.Default.Lightbulb, "Ideias"),
            NavigationItem(Screen.ManagerProjects, Icons.Default.Work, "Projetos"),
            NavigationItem(Screen.OperatorStrategy, Icons.AutoMirrored.Filled.Assignment, "Estratégia"),
            NavigationItem(Screen.Profile, Icons.Default.Person, "Perfil")
        )
        Role.LIDERANCA -> listOf(
            NavigationItem(Screen.LeadershipDashboard, Icons.Default.Home, "Início"),
            NavigationItem(Screen.LeadershipStrategy, Icons.Default.TrackChanges, "Estratégia"),
            NavigationItem(Screen.LeadershipProjects, Icons.Default.Folder, "Projetos"),
            NavigationItem(Screen.LeadershipResults, Icons.Default.BarChart, "Resultados"),
            NavigationItem(Screen.Profile, Icons.Default.Person, "Perfil")
        )
    }

    NavigationBar(
        containerColor = AguiaColors.CardWhite,
        contentColor = AguiaColors.PrimaryBlue
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.screen.route
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = isSelected,
                onClick = { onNavigate(item.screen.route) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = AguiaColors.PrimaryBlue,
                    selectedTextColor = AguiaColors.PrimaryBlue,
                    unselectedIconColor = AguiaColors.TextSecondary,
                    unselectedTextColor = AguiaColors.TextSecondary,
                    indicatorColor = AguiaColors.PrimaryBlue.copy(alpha = 0.1f)
                )
            )
        }
    }
}
