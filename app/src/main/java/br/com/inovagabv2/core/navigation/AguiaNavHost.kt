package br.com.inovagabv2.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import br.com.inovagabv2.core.session.AppInitState
import br.com.inovagabv2.core.session.SessionExpiryPolicy
import br.com.inovagabv2.domain.model.Role
import br.com.inovagabv2.presentation.auth.LoginScreen
import br.com.inovagabv2.presentation.auth.LoginViewModel
import br.com.inovagabv2.presentation.auth.RegisterScreen
import br.com.inovagabv2.presentation.auth.RegisterViewModel
import br.com.inovagabv2.presentation.leadership.*
import br.com.inovagabv2.presentation.manager.home.ManagerHomeScreen
import br.com.inovagabv2.presentation.manager.ideas.ManagerIdeasScreen
import br.com.inovagabv2.presentation.manager.ideas.details.ManagerIdeaDetailsScreen
import br.com.inovagabv2.presentation.manager.projects.ManagerProjectsScreen
import br.com.inovagabv2.presentation.manager.projects.create.ManagerCreateProjectScreen
import br.com.inovagabv2.presentation.manager.projects.details.ManagerProjectDetailsScreen
import br.com.inovagabv2.presentation.manager.strategy.ManagerStrategyScreen
import br.com.inovagabv2.presentation.onboarding.OnboardingScreen
import br.com.inovagabv2.presentation.onboarding.OnboardingViewModel
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
import br.com.inovagabv2.presentation.profile.ProfileScreen
import br.com.inovagabv2.presentation.profile.ProfileViewModel
import br.com.inovagabv2.presentation.ranking.InnovationRankingScreen
import br.com.inovagabv2.presentation.splash.SplashScreen

internal fun tabHomeRoute(role: Role): String = when (role) {
    Role.OPERADOR -> Screen.OperatorHome.route
    Role.GESTOR -> Screen.ManagerHome.route
    Role.LIDERANCA -> Screen.LeadershipDashboard.route
}

private fun NavHostController.navigateToTab(route: String, startRoute: String) {
    navigate(route) {
        popUpTo(startRoute) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
fun AguiaNavHost(
    navController: NavHostController,
    appInitState: AppInitState = AppInitState.Loading,
    modifier: Modifier = Modifier,
    startDestination: String = Screen.Splash.route,
    consumeManualLogout: () -> Boolean = { false },
) {
    val sessionExpiryPolicy = remember { SessionExpiryPolicy() }
    var sessionExpiredMessage by remember { mutableStateOf<String?>(null) }
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    LaunchedEffect(appInitState, currentRoute) {
        val manualLogoutMark = if (appInitState is AppInitState.Unauthenticated) {
            consumeManualLogout()
        } else {
            false
        }
        val message = sessionExpiryPolicy.onAppInitState(
            state = appInitState,
            currentRoute = currentRoute,
            manualLogoutMark = manualLogoutMark,
        )
        if (message != null) {
            sessionExpiredMessage = message
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                appInitState = appInitState,
                onNavigate = { targetRoute ->
                    navController.navigate(targetRoute) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Onboarding.route) {
            val viewModel: OnboardingViewModel = hiltViewModel()
            OnboardingScreen(
                viewModel = viewModel,
                onFinish = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            val viewModel: LoginViewModel = hiltViewModel()
            LoginScreen(
                viewModel = viewModel,
                sessionExpiredMessage = sessionExpiredMessage,
                onSessionExpiredMessageShown = { sessionExpiredMessage = null },
                onBackClick = if (navController.previousBackStackEntry != null) {
                    { navController.popBackStack() }
                } else {
                    null
                },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
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

        composable(Screen.Register.route) {
            val viewModel: RegisterViewModel = hiltViewModel()
            RegisterScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onNavigateToLogin = { navController.popBackStack() },
                onRegisterSuccess = { role ->
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
                onNavigateToStrategies = {
                    navController.navigateToTab(Screen.OperatorStrategy.route, Screen.OperatorHome.route)
                },
                onNavigateToMyIdeas = {
                    navController.navigateToTab(Screen.MyIdeas.route, Screen.OperatorHome.route)
                },
                onNavigateToIdeaDetails = { ideaId -> 
                    navController.navigate(Screen.IdeaDetails.createRoute(ideaId)) 
                },
                onNavigateToProfile = {
                    navController.navigateToTab(Screen.Profile.route, Screen.OperatorHome.route)
                }
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
                onNavigateToHome = {
                    navController.navigateToTab(Screen.OperatorHome.route, Screen.OperatorHome.route)
                },
                onNavigateToSugestoes = {
                    navController.navigateToTab(Screen.MyIdeas.route, Screen.OperatorHome.route)
                },
                onNavigateToProfile = {
                    navController.navigateToTab(Screen.Profile.route, Screen.OperatorHome.route)
                }
            )
        }

        composable(
            route = Screen.StrategyDetails.route,
            arguments = listOf(
                androidx.navigation.navArgument("strategyId") { type = androidx.navigation.NavType.StringType }
            )
        ) {
            StrategyDetailsScreen(
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
                onNavigateToCreateIdea = { navController.navigate(Screen.CreateIdea.route) },
                onNavigateToHome = {
                    navController.navigateToTab(Screen.OperatorHome.route, Screen.OperatorHome.route)
                },
                onNavigateToStrategies = {
                    navController.navigateToTab(Screen.OperatorStrategy.route, Screen.OperatorHome.route)
                },
                onNavigateToProfile = {
                    navController.navigateToTab(Screen.Profile.route, Screen.OperatorHome.route)
                }
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
                onNavigateToIdeas = {
                    navController.navigateToTab(Screen.ManagerIdeas.route, Screen.ManagerHome.route)
                },
                onNavigateToIdeaDetails = { ideaId ->
                    navController.navigate(Screen.ManagerIdeaDetails.createRoute(ideaId))
                },
                onNavigateToProjects = {
                    navController.navigateToTab(Screen.ManagerProjects.route, Screen.ManagerHome.route)
                },
                onNavigateToStrategy = {
                    navController.navigateToTab(Screen.ManagerStrategy.route, Screen.ManagerHome.route)
                },
                onNavigateToProfile = {
                    navController.navigateToTab(Screen.Profile.route, Screen.ManagerHome.route)
                }
            )
        }

        composable(Screen.ManagerStrategy.route) {
            ManagerStrategyScreen(
                onNavigateToHome = {
                    navController.navigateToTab(Screen.ManagerHome.route, Screen.ManagerHome.route)
                },
                onNavigateToIdeas = {
                    navController.navigateToTab(Screen.ManagerIdeas.route, Screen.ManagerHome.route)
                },
                onNavigateToProjects = {
                    navController.navigateToTab(Screen.ManagerProjects.route, Screen.ManagerHome.route)
                },
                onNavigateToProfile = {
                    navController.navigateToTab(Screen.Profile.route, Screen.ManagerHome.route)
                },
                onStrategyClick = { strategyId -> navController.navigate(Screen.StrategyDetails.createRoute(strategyId)) }
            )
        }

        composable(Screen.ManagerIdeas.route) {
            ManagerIdeasScreen(
                onNavigateToDetails = { ideaId ->
                    navController.navigate(Screen.ManagerIdeaDetails.createRoute(ideaId))
                },
                onBackClick = { navController.popBackStack() },
                onNavigateToHome = {
                    navController.navigateToTab(Screen.ManagerHome.route, Screen.ManagerHome.route)
                },
                onNavigateToProjects = {
                    navController.navigateToTab(Screen.ManagerProjects.route, Screen.ManagerHome.route)
                },
                onNavigateToStrategy = {
                    navController.navigateToTab(Screen.ManagerStrategy.route, Screen.ManagerHome.route)
                },
                onNavigateToProfile = {
                    navController.navigateToTab(Screen.Profile.route, Screen.ManagerHome.route)
                }
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
                onNavigateToCreateProject = { navController.navigate(Screen.ManagerCreateProject.route) },
                onNavigateToHome = {
                    navController.navigateToTab(Screen.ManagerHome.route, Screen.ManagerHome.route)
                },
                onNavigateToIdeas = {
                    navController.navigateToTab(Screen.ManagerIdeas.route, Screen.ManagerHome.route)
                },
                onNavigateToStrategy = {
                    navController.navigateToTab(Screen.ManagerStrategy.route, Screen.ManagerHome.route)
                },
                onNavigateToProfile = {
                    navController.navigateToTab(Screen.Profile.route, Screen.ManagerHome.route)
                }
            )
        }

        composable(Screen.ManagerCreateProject.route) {
            ManagerCreateProjectScreen(
                onBackClick = { navController.popBackStack() },
                onSuccess = { navController.popBackStack() }
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
                onNavigateToProjects = {
                    navController.navigateToTab(Screen.LeadershipProjects.route, Screen.LeadershipDashboard.route)
                },
                onNavigateToIdeas = {
                    navController.navigateToTab(Screen.LeadershipIdeas.route, Screen.LeadershipDashboard.route)
                },
                onNavigateToStrategy = {
                    navController.navigateToTab(Screen.LeadershipStrategy.route, Screen.LeadershipDashboard.route)
                },
                onNavigateToProfile = {
                    navController.navigateToTab(Screen.Profile.route, Screen.LeadershipDashboard.route)
                }
            )
        }

        composable(Screen.LeadershipIdeas.route) {
            val viewModel: LeadershipIdeasViewModel = hiltViewModel()
            LeadershipIdeasScreen(
                onNavigateToDetails = { ideaId -> navController.navigate(Screen.IdeaDetails.createRoute(ideaId)) },
                onNavigateToHome = {
                    navController.navigateToTab(Screen.LeadershipDashboard.route, Screen.LeadershipDashboard.route)
                },
                onNavigateToProjects = {
                    navController.navigateToTab(Screen.LeadershipProjects.route, Screen.LeadershipDashboard.route)
                },
                onNavigateToStrategy = {
                    navController.navigateToTab(Screen.LeadershipStrategy.route, Screen.LeadershipDashboard.route)
                },
                onNavigateToProfile = {
                    navController.navigateToTab(Screen.Profile.route, Screen.LeadershipDashboard.route)
                },
                viewModel = viewModel
            )
        }

        composable(Screen.LeadershipStrategy.route) {
            val viewModel: LeadershipStrategyViewModel = hiltViewModel()
            LeadershipStrategyScreen(
                viewModel = viewModel,
                onNavigateToCreate = { navController.navigate(Screen.CreateStrategy.route) },
                onNavigateToEdit = { strategyId -> navController.navigate(Screen.EditStrategy.createRoute(strategyId)) },
                onNavigateToDashboard = {
                    navController.navigateToTab(Screen.LeadershipDashboard.route, Screen.LeadershipDashboard.route)
                },
                onNavigateToProjects = {
                    navController.navigateToTab(Screen.LeadershipProjects.route, Screen.LeadershipDashboard.route)
                },
                onNavigateToIdeas = {
                    navController.navigateToTab(Screen.LeadershipIdeas.route, Screen.LeadershipDashboard.route)
                },
                onNavigateToProfile = {
                    navController.navigateToTab(Screen.Profile.route, Screen.LeadershipDashboard.route)
                },
                onNavigateToPanel = { strategyId -> navController.navigate(Screen.StrategyDashboard.createRoute(strategyId)) }
            )
        }

        composable(
            route = Screen.EditStrategy.route,
            arguments = listOf(
                androidx.navigation.navArgument("strategyId") { type = androidx.navigation.NavType.StringType }
            )
        ) {
            EditStrategyScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.StrategyDashboard.route,
            arguments = listOf(
                androidx.navigation.navArgument("strategyId") { type = androidx.navigation.NavType.StringType }
            )
        ) {
            val viewModel: StrategyDashboardViewModel = hiltViewModel()
            StrategyDashboardScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onIdeaClick = { ideaId -> navController.navigate(Screen.IdeaDetails.createRoute(ideaId)) },
                onNavigateToHome = {
                    navController.navigateToTab(Screen.LeadershipDashboard.route, Screen.LeadershipDashboard.route)
                },
                onNavigateToProjects = {
                    navController.navigateToTab(Screen.LeadershipProjects.route, Screen.LeadershipDashboard.route)
                },
                onNavigateToIdeas = {
                    navController.navigateToTab(Screen.LeadershipIdeas.route, Screen.LeadershipDashboard.route)
                },
                onNavigateToProfile = {
                    navController.navigateToTab(Screen.Profile.route, Screen.LeadershipDashboard.route)
                }
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
                onNavigateToDashboard = {
                    navController.navigateToTab(Screen.LeadershipDashboard.route, Screen.LeadershipDashboard.route)
                },
                onNavigateToIdeas = {
                    navController.navigateToTab(Screen.LeadershipIdeas.route, Screen.LeadershipDashboard.route)
                },
                onNavigateToStrategy = {
                    navController.navigateToTab(Screen.LeadershipStrategy.route, Screen.LeadershipDashboard.route)
                },
                onNavigateToProfile = {
                    navController.navigateToTab(Screen.Profile.route, Screen.LeadershipDashboard.route)
                },
                onNavigateToDetails = { projectId -> navController.navigate(Screen.LeadershipProjectDetails.createRoute(projectId)) }
            )
        }

        composable(
            route = Screen.LeadershipProjectDetails.route,
            arguments = listOf(
                androidx.navigation.navArgument("projectId") { type = androidx.navigation.NavType.StringType }
            )
        ) {
            LeadershipProjectDetailsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.LeadershipProjectDashboard.route,
            arguments = listOf(
                androidx.navigation.navArgument("projectId") { type = androidx.navigation.NavType.StringType }
            )
        ) {
            LeadershipProjectDashboardScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.LeadershipResults.route) {
            val viewModel: LeadershipResultsViewModel = hiltViewModel()
            LeadershipResultsScreen(
                viewModel = viewModel,
                onNavigateToDashboard = {
                    navController.navigateToTab(Screen.LeadershipDashboard.route, Screen.LeadershipDashboard.route)
                },
                onNavigateToProjects = {
                    navController.navigateToTab(Screen.LeadershipProjects.route, Screen.LeadershipDashboard.route)
                },
                onNavigateToStrategy = {
                    navController.navigateToTab(Screen.LeadershipStrategy.route, Screen.LeadershipDashboard.route)
                },
                onNavigateToProfile = {
                    navController.navigateToTab(Screen.Profile.route, Screen.LeadershipDashboard.route)
                }
            )
        }

        composable(Screen.InnovationRanking.route) {
            InnovationRankingScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.Profile.route) {
            val viewModel: ProfileViewModel = hiltViewModel()
            
            ProfileScreen(
                viewModel = viewModel,
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onBottomNavigate = { targetRoute, startRouteFromBar ->
                    val startRoute = (appInitState as? AppInitState.Authenticated)
                        ?.user
                        ?.role
                        ?.let(::tabHomeRoute)
                        ?: startRouteFromBar
                    navController.navigateToTab(targetRoute, startRoute)
                },
                onNavigateToRanking = {
                    navController.navigate(Screen.InnovationRanking.route) {
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}
