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
import br.com.inovagabv2.core.session.AppInitState
import br.com.inovagabv2.core.session.SessionManager
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
            val appInitState by sessionManager.appInitState.collectAsState(initial = AppInitState.Loading)
            val navController = rememberNavController()

            AguiaTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AguiaNavHost(
                        navController = navController,
                        appInitState = appInitState,
                        startDestination = Screen.Splash.route,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
