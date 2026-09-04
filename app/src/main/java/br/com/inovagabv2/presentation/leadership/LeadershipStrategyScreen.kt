package br.com.inovagabv2.presentation.leadership

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.inovagabv2.core.designsystem.AguiaColors
import br.com.inovagabv2.core.designsystem.components.*
import br.com.inovagabv2.core.navigation.Screen
import br.com.inovagabv2.domain.model.Role

@Composable
fun LeadershipStrategyScreen(
    viewModel: LeadershipStrategyViewModel,
    onNavigateToCreate: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    onNavigateToDashboard: () -> Unit,
    onNavigateToProjects: () -> Unit,
    onNavigateToResults: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    val tabs = listOf("Publicadas", "Rascunhos")

    Scaffold(
        topBar = {
            AguiaTopBar(showLeadershipTag = true)
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToCreate,
                containerColor = AguiaColors.PrimaryBlue,
                contentColor = Color.White,
                shape = RoundedCornerShape(24.dp),
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Nova diretriz", fontWeight = FontWeight.SemiBold) }
            )
        },
        bottomBar = {
            AguiaBottomBar(
                currentRoute = Screen.LeadershipStrategy.route,
                role = Role.LIDERANCA,
                onNavigate = { route ->
                    when (route) {
                        Screen.LeadershipDashboard.route -> onNavigateToDashboard()
                        Screen.LeadershipProjects.route -> onNavigateToProjects()
                        Screen.LeadershipResults.route -> onNavigateToResults()
                        Screen.LeadershipStrategy.route -> { /* Already here */ }
                        Screen.Profile.route -> onNavigateToProfile()
                    }
                }
            )
        },
        containerColor = AguiaColors.Background
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Search Bar & Filter Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Pesquisar estratégias...", color = AguiaColors.TextSecondary, fontSize = 14.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = AguiaColors.TextSecondary) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = AguiaColors.CardWhite,
                        unfocusedContainerColor = AguiaColors.CardWhite,
                        focusedBorderColor = AguiaColors.PrimaryBlue.copy(alpha = 0.3f),
                        unfocusedBorderColor = Color.Transparent
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = { /* Filter action */ },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "Filtrar",
                        tint = AguiaColors.NavyDark
                    )
                }
            }

            // Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = AguiaColors.Background,
                contentColor = AguiaColors.PrimaryBlue,
                indicator = { tabPositions ->
                    if (selectedTab < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = AguiaColors.PrimaryBlue
                        )
                    }
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                                color = if (selectedTab == index) AguiaColors.PrimaryBlue else AguiaColors.TextSecondary
                            )
                        }
                    )
                }
            }

            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AguiaColors.PrimaryBlue)
                }
            } else {
                val filteredStrategies = state.strategies.filter { strategy ->
                    val matchesTab = if (selectedTab == 0) strategy.isPublished else !strategy.isPublished
                    val matchesQuery = searchQuery.isBlank() || 
                        strategy.title.contains(searchQuery, ignoreCase = true) ||
                        strategy.description.contains(searchQuery, ignoreCase = true)
                    matchesTab && matchesQuery
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredStrategies) { strategy ->
                        AguiaStrategyCard(
                            strategy = strategy,
                            onClick = { onNavigateToEdit(strategy.id) },
                            onEdit = { onNavigateToEdit(strategy.id) },
                            onDelete = { viewModel.deleteStrategy(strategy.id) }
                        )
                    }
                }
            }
        }
    }
}
