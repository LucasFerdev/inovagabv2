package br.com.inovagabv2.presentation.manager.projects.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.inovagabv2.core.designsystem.AguiaColors
import br.com.inovagabv2.core.designsystem.components.*

@Composable
fun ManagerProjectDetailsScreen(
    onBackClick: () -> Unit,
    viewModel: ManagerProjectDetailsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            AguiaTopBar(
                title = "Detalhes do Projeto",
                onBackClick = onBackClick,
                backgroundColor = AguiaColors.ManagerPurple,
                contentColor = AguiaColors.CardWhite
            )
        },
        containerColor = AguiaColors.Background
    ) { padding ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AguiaColors.ManagerPurple)
            }
        } else if (state.project != null) {
            val project = state.project!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header Info
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = AguiaColors.CardWhite)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = project.name,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = AguiaColors.TextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = project.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = AguiaColors.TextSecondary
                        )
                    }
                }

                // Progress Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = AguiaColors.CardWhite)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Progresso Atual",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AguiaColors.TextPrimary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        AguiaProgressBar(
                            progress = project.progress,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        AguiaButton(
                            text = "Atualizar progresso",
                            onClick = { /* Open update dialog or similar */ },
                            outline = true,
                            containerColor = AguiaColors.ManagerPurple
                        )
                    }
                }

                // Details Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AguiaMetricCard(
                        label = "Prazo",
                        value = project.deadline,
                        color = AguiaColors.TextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    AguiaMetricCard(
                        label = "Investimento",
                        value = project.investment,
                        color = AguiaColors.TextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Results Section
                if (project.results.isNotEmpty()) {
                    Text(
                        text = "Resultados Alcançados",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AguiaColors.TextPrimary
                    )
                    project.results.forEach { metric ->
                        AguiaMetricCard(
                            label = metric.label,
                            value = metric.value,
                            color = AguiaColors.SuccessGreen,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                // Next Steps
                if (project.nextSteps.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = AguiaColors.CardWhite)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Próximos Passos",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = AguiaColors.TextPrimary
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            project.nextSteps.forEach { step ->
                                Row(
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(selected = false, onClick = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = step,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = AguiaColors.TextPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
