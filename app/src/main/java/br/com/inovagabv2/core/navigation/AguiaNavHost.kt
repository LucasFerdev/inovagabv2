package br.com.inovagabv2.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import br.com.inovagabv2.domain.model.Role
import br.com.inovagabv2.presentation.auth.LoginScreen
import br.com.inovagabv2.presentation.auth.LoginViewModel

import br.com.inovagabv2.presentation.operator.create.CreateIdeaScreen
import br.com.inovagabv2.presentation.operator.create.CreateIdeaViewModel
import br.com.inovagabv2.presentation.operator.details.IdeaDetailsScreen
import br.com.inovagabv2.presentation.operator.details.IdeaDetailsViewModel
import br.com.inovagabv2.presentation.operator.home.OperatorHomeScreen
import br.com.inovagabv2.presentation.operator.home.OperatorHomeViewModel
import br.com.inovagabv2.presentation.operator.myideas.MyIdeasScreen
import br.com.inovagabv2.presentation.operator.myideas.MyIdeasViewModel
import br.com.inovagabv2.presentation.operator.strategy.OperatorStrategyScreen
import br.com.inovagabv2.presentation.operator.strategy.OperatorStrategyViewModel
import br.com.inovagabv2.presentation.operator.strategy.StrategyDetailsScreen
import br.com.inovagabv2.presentation.operator.communications.CommunicationsScreen
import br.com.inovagabv2.presentation.profile.ProfileScreen
import br.com.inovagabv2.presentation.profile.ProfileViewModel

import br.com.inovagabv2.presentation.manager.home.ManagerHomeScreen
import br.com.inovagabv2.presentation.manager.home.ManagerHomeViewModel
import br.com.inovagabv2.presentation.manager.ideas.ManagerIdeasScreen
import br.com.inovagabv2.presentation.manager.ideas.ManagerIdeasViewModel
import br.com.inovagabv2.presentation.manager.ideas.details.ManagerIdeaDetailsScreen
import br.com.inovagabv2.presentation.manager.ideas.details.ManagerIdeaDetailsViewModel
import br.com.inovagabv2.presentation.manager.projects.ManagerProjectsScreen
import br.com.inovagabv2.presentation.manager.projects.ManagerProjectsViewModel
import br.com.inovagabv2.presentation.manager.projects.details.ManagerProjectDetailsScreen
import br.com.inovagabv2.presentation.manager.projects.details.ManagerProjectDetailsViewModel

import br.com.inovagabv2.presentation.splash.SplashScreen
import br.com.inovagabv2.domain.model.User
import br.com.inovagabv2.presentation.leadership.*

@Composable
fun AguiaNavHost(
    navController: NavHostController,
    user: User? = null,
    modifier: Modifier = Modifier,
    startDestination: String = Screen.Splash.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onSplashFinished = {
                    val targetRoute = if (user != null) {
                        when (user.role) {
                            Role.OPERADOR -> Screen.OperatorHome.route
                            Role.GESTOR -> Screen.ManagerHome.route
                            Role.LIDERANCA -> Screen.LeadershipDashboard.route
                        }
                    } else {
                        Screen.Login.route
                    }
                    navController.navigate(targetRoute) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            val viewModel: LoginViewModel = hiltViewModel()
            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = { role ->
                    val route = when (role) {
                        Role.OPERADOR -> Screen.OperatorHome.route
                        Role.GESTOR -> Screen.ManagerHome.route
                        Role.LIDERANCA -> Screen.LeadershipDashboard.route
                    }
                    navController.navigate(route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        
        // Operador
        composable(Screen.OperatorHome.route) {
            val viewModel: OperatorHomeViewModel = hiltViewModel()
            OperatorHomeScreen(
                viewModel = viewModel,
                onNavigateToCreateIdea = { navController.navigate(Screen.CreateIdea.route) },
                onNavigateToStrategies = { navController.navigate(Screen.OperatorStrategy.route) },
                onNavigateToMyIdeas = { navController.navigate(Screen.MyIdeas.route) },
                onNavigateToIdeaDetails = { ideaId -> 
                    navController.navigate(Screen.IdeaDetails.createRoute(ideaId)) 
                },
                onNavigateToCommunications = { navController.navigate(Screen.OperatorCommunications.route) },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) }
            )
        }

        composable(Screen.OperatorStrategy.route) {
            val viewModel: OperatorStrategyViewModel = hiltViewModel()
            OperatorStrategyScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onStrategyClick = { strategyId ->
                    navController.navigate(Screen.StrategyDetails.createRoute(strategyId))
                },
                onNavigateToHome = { navController.navigate(Screen.OperatorHome.route) },
                onNavigateToSugestoes = { navController.navigate(Screen.MyIdeas.route) },
                onNavigateToCommunications = { navController.navigate(Screen.OperatorCommunications.route) },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) }
            )
        }

        composable(
            route = Screen.StrategyDetails.route,
            arguments = listOf(
                androidx.navigation.navArgument("strategyId") { type = androidx.navigation.NavType.StringType }
            )
        ) { backStackEntry ->
            val strategyId = backStackEntry.arguments?.getString("strategyId") ?: ""
            val viewModel: OperatorStrategyViewModel = hiltViewModel()
            StrategyDetailsScreen(
                viewModel = viewModel,
                strategyId = strategyId,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.CreateIdea.route) {
            val viewModel: CreateIdeaViewModel = hiltViewModel()
            CreateIdeaScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onSuccess = { 
                    navController.navigate(Screen.MyIdeas.route) {
                        popUpTo(Screen.OperatorHome.route)
                    }
                }
            )
        }

        composable(Screen.MyIdeas.route) {
            val viewModel: MyIdeasViewModel = hiltViewModel()
            MyIdeasScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onIdeaClick = { ideaId ->
                    navController.navigate(Screen.IdeaDetails.createRoute(ideaId))
                },
                onNavigateToHome = { navController.navigate(Screen.OperatorHome.route) },
                onNavigateToCommunications = { navController.navigate(Screen.OperatorCommunications.route) },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) }
            )
        }

        composable(Screen.OperatorCommunications.route) {
            CommunicationsScreen(
                onNavigateToHome = { navController.navigate(Screen.OperatorHome.route) },
                onNavigateToSugestoes = { navController.navigate(Screen.MyIdeas.route) },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) }
            )
        }

        composable(
            route = Screen.IdeaDetails.route,
            arguments = listOf(
                androidx.navigation.navArgument("ideaId") { type = androidx.navigation.NavType.StringType }
            )
        ) {
            val viewModel: IdeaDetailsViewModel = hiltViewModel()
            IdeaDetailsScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
        
        // Gestor
        composable(Screen.ManagerHome.route) {
            ManagerHomeScreen(
                onNavigateToIdeas = { navController.navigate(Screen.ManagerIdeas.route) },
                onNavigateToIdeaDetails = { ideaId ->
                    navController.navigate(Screen.ManagerIdeaDetails.createRoute(ideaId))
                },
                onNavigateToProjects = { navController.navigate(Screen.ManagerProjects.route) },
                onNavigateToStrategy = { navController.navigate(Screen.OperatorStrategy.route) },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) }
            )
        }

        composable(Screen.ManagerIdeas.route) {
            ManagerIdeasScreen(
                onNavigateToDetails = { ideaId ->
                    navController.navigate(Screen.ManagerIdeaDetails.createRoute(ideaId))
                },
                onBackClick = { navController.popBackStack() },
                onNavigateToHome = { navController.navigate(Screen.ManagerHome.route) },
                onNavigateToProjects = { navController.navigate(Screen.ManagerProjects.route) },
                onNavigateToStrategy = { navController.navigate(Screen.OperatorStrategy.route) },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) }
            )
        }

        composable(
            route = Screen.ManagerIdeaDetails.route,
            arguments = listOf(
                androidx.navigation.navArgument("ideaId") { type = androidx.navigation.NavType.StringType }
            )
        ) {
            ManagerIdeaDetailsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.ManagerProjects.route) {
            ManagerProjectsScreen(
                onNavigateToDetails = { projectId ->
                    navController.navigate(Screen.ManagerProjectDetails.createRoute(projectId))
                },
                onNavigateToCreateProject = { /* Implement if needed */ },
                onNavigateToHome = { navController.navigate(Screen.ManagerHome.route) },
                onNavigateToIdeas = { navController.navigate(Screen.ManagerIdeas.route) },
                onNavigateToStrategy = { navController.navigate(Screen.OperatorStrategy.route) },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) }
            )
        }

        composable(
            route = Screen.ManagerProjectDetails.route,
            arguments = listOf(
                androidx.navigation.navArgument("projectId") { type = androidx.navigation.NavType.StringType }
            )
        ) {
            ManagerProjectDetailsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        
        // Liderança
        composable(Screen.LeadershipDashboard.route) {
            val viewModel: LeadershipDashboardViewModel = hiltViewModel()
            LeadershipDashboardScreen(
                viewModel = viewModel,
                onNavigateToProjects = { navController.navigate(Screen.LeadershipProjects.route) },
                onNavigateToResults = { navController.navigate(Screen.LeadershipResults.route) },
                onNavigateToStrategy = { navController.navigate(Screen.LeadershipStrategy.route) },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) }
            )
        }

        composable(Screen.LeadershipStrategy.route) {
            val viewModel: LeadershipStrategyViewModel = hiltViewModel()
            LeadershipStrategyScreen(
                viewModel = viewModel,
                onNavigateToCreate = { navController.navigate(Screen.CreateStrategy.route) },
                onNavigateToEdit = { /* ideaId -> navController.navigate(Screen.LeadershipStrategyEdit.createRoute(ideaId)) */ },
                onNavigateToDashboard = { navController.navigate(Screen.LeadershipDashboard.route) },
                onNavigateToProjects = { navController.navigate(Screen.LeadershipProjects.route) },
                onNavigateToResults = { navController.navigate(Screen.LeadershipResults.route) },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) }
            )
        }

        composable(Screen.CreateStrategy.route) {
            val viewModel: CreateStrategyViewModel = hiltViewModel()
            CreateStrategyScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onSuccess = { navController.popBackStack() }
            )
        }

        composable(Screen.LeadershipProjects.route) {
            val viewModel: LeadershipProjectsViewModel = hiltViewModel()
            LeadershipProjectsScreen(
                viewModel = viewModel,
                onNavigateToDashboard = { navController.navigate(Screen.LeadershipDashboard.route) },
                onNavigateToResults = { navController.navigate(Screen.LeadershipResults.route) },
                onNavigateToStrategy = { navController.navigate(Screen.LeadershipStrategy.route) },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) }
            )
        }

        composable(Screen.LeadershipResults.route) {
            val viewModel: LeadershipResultsViewModel = hiltViewModel()
            LeadershipResultsScreen(
                viewModel = viewModel,
                onNavigateToDashboard = { navController.navigate(Screen.LeadershipDashboard.route) },
                onNavigateToProjects = { navController.navigate(Screen.LeadershipProjects.route) },
                onNavigateToStrategy = { navController.navigate(Screen.LeadershipStrategy.route) },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) }
            )
        }

        composable(Screen.Profile.route) {
            val viewModel: ProfileViewModel = hiltViewModel()
            val userState by viewModel.user.collectAsState()
            
            ProfileScreen(
                viewModel = viewModel,
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    val route = when (userState?.role) {
                        Role.OPERADOR -> Screen.OperatorHome.route
                        Role.GESTOR -> Screen.ManagerHome.route
                        Role.LIDERANCA -> Screen.LeadershipDashboard.route
                        else -> Screen.Login.route
                    }
                    navController.navigate(route) {
                        popUpTo(Screen.Profile.route) { inclusive = true }
                    }
                },
                onNavigateToSugestoes = {
                    val route = if (userState?.role == Role.GESTOR) Screen.ManagerIdeas.route else Screen.MyIdeas.route
                    navController.navigate(route)
                },
                onNavigateToCommunications = {
                    val route = when (userState?.role) {
                        Role.GESTOR -> Screen.ManagerProjects.route
                        Role.LIDERANCA -> Screen.LeadershipProjects.route
                        else -> Screen.OperatorCommunications.route
                    }
                    navController.navigate(route)
                }
            )
        }
    }
}
