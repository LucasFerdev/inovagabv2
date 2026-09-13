package br.com.inovagabv2.data.repository

import br.com.inovagabv2.domain.model.DashboardData
import br.com.inovagabv2.domain.model.IdeaStatus
import br.com.inovagabv2.domain.model.ProjectStatus
import br.com.inovagabv2.domain.repository.DashboardRepository
import br.com.inovagabv2.domain.repository.IdeaRepository
import br.com.inovagabv2.domain.repository.ProjectRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DashboardRepositoryImpl @Inject constructor(
    private val ideaRepository: IdeaRepository,
    private val projectRepository: ProjectRepository
) : DashboardRepository {

    override fun getDashboardData(): Flow<DashboardData> {
        return combine(
            ideaRepository.getIdeas(),
            projectRepository.getProjects()
        ) { ideas, projects ->
            val approvedCount = ideas.count { it.status == IdeaStatus.APROVADA }
            val inAnalysisCount = ideas.count { it.status == IdeaStatus.EM_ANALISE }
            val activeProjects = projects.filter { it.status == ProjectStatus.EM_ANDAMENTO || it.status == ProjectStatus.PLANEJADO }
            val statusMap = projects.groupingBy { it.status }.eachCount()

            var totalInvest = 0.0
            val totalRet = 0.0
            projects.forEach { proj ->
                totalInvest += parseCurrency(proj.investment)
            }
            val profitVal = (totalRet - totalInvest).coerceAtLeast(0.0)
            val roiVal = if (totalInvest > 0) ((totalRet - totalInvest) / totalInvest) * 100 else 0.0

            DashboardData(
                roi = String.format(Locale.US, "%.1f%%", roiVal).replace(".", ","),
                profit = formatCurrency(profitVal),
                costReduction = "R$ 0,00",
                productivity = "0%",
                investment = totalInvest,
                financialReturn = totalRet,
                roiPercentage = roiVal,
                activeProjectsCount = activeProjects.size,
                delayedProjectsCount = 0,
                approvedIdeasCount = approvedCount,
                inAnalysisIdeasCount = inAnalysisCount,
                projectsByStatus = statusMap
            )
        }
    }

    private fun parseCurrency(value: String?): Double {
        if (value.isNullOrBlank()) return 0.0
        val clean = value.replace("[^0-9,]".toRegex(), "").replace(",", ".")
        return clean.toDoubleOrNull() ?: 0.0
    }

    private fun formatCurrency(value: Double): String {
        return when {
            value >= 1_000_000 -> String.format(Locale.US, "R$ %.2f mi", value / 1_000_000).replace(".", ",")
            value >= 1_000 -> String.format(Locale.US, "R$ %.0f mil", value / 1_000)
            else -> String.format(Locale.US, "R$ %.2f", value).replace(".", ",")
        }
    }
}
