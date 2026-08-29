package br.com.inovagabv2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import br.com.inovagabv2.core.designsystem.AguiaTheme
import br.com.inovagabv2.core.navigation.AguiaNavHost
import br.com.inovagabv2.core.navigation.Screen
import br.com.inovagabv2.core.session.SessionManager
import br.com.inovagabv2.domain.model.Role
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val user by sessionManager.userSession.collectAsState(initial = null)
            val navController = rememberNavController()

            AguiaTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Determine start destination based on session
                    val startDestination = if (user != null) {
                        when (user!!.role) {
                            Role.OPERADOR -> Screen.OperatorHome.route
                            Role.GESTOR -> Screen.ManagerHome.route
                            Role.LIDERANCA -> Screen.LeadershipDashboard.route
                        }
                    } else {
                        Screen.Login.route
                    }

                    AguiaNavHost(
                        navController = navController,
                        startDestination = startDestination,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
