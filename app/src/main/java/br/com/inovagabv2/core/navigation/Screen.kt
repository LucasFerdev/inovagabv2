package br.com.inovagabv2.core.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object Register : Screen("register")
    
    // Shared
    object Profile : Screen("profile")

    // Operador
    object OperatorHome : Screen("operator_home")
    object OperatorStrategy : Screen("operator_strategy")
    object StrategyDetails : Screen("strategy_details/{strategyId}") {
        fun createRoute(strategyId: String) = "strategy_details/$strategyId"
    }
    object CreateIdea : Screen("create_idea")
    object MyIdeas : Screen("my_ideas")
    object OperatorCommunications : Screen("operator_communications")
    object IdeaDetails : Screen("idea_details/{ideaId}") {
        fun createRoute(ideaId: String) = "idea_details/$ideaId"
    }
    
    // Gestor
    object ManagerHome : Screen("manager_home")
    object ManagerIdeas : Screen("manager_ideas")
    object ManagerIdeaDetails : Screen("manager_idea_details/{ideaId}") {
        fun createRoute(ideaId: String) = "manager_idea_details/$ideaId"
    }
    object ManagerProjects : Screen("manager_projects")
    object ManagerProjectDetails : Screen("manager_project_details/{projectId}") {
        fun createRoute(projectId: String) = "manager_project_details/$projectId"
    }
    
    // Liderança
    object LeadershipDashboard : Screen("leadership_dashboard")
    object LeadershipStrategy : Screen("leadership_strategy")
    object CreateStrategy : Screen("create_strategy")
    object LeadershipProjects : Screen("leadership_projects")
    object LeadershipResults : Screen("leadership_results")
    object StrategyDashboard : Screen("strategy_dashboard/{strategyId}") {
        fun createRoute(strategyId: String) = "strategy_dashboard/$strategyId"
    }
}
