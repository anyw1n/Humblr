package com.example.humblr

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.rememberNavController
import com.example.humblr.data.Api
import com.example.humblr.data.CredentialsRepository
import com.example.humblr.ui.MainDestinations
import com.example.humblr.ui.NavGraph
import com.example.humblr.ui.common.HomeBottomBar
import com.example.humblr.ui.theme.HumblrTheme
import com.example.humblr.util.OnboardingCompleteKey
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var credentialsRepository: CredentialsRepository

    @Inject
    lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val onboardingComplete = prefs.getBoolean(OnboardingCompleteKey, false)
        val userLoggedIn = credentialsRepository.token != null
        setContent {
            HumblrTheme {
                val navController = rememberNavController()
                val snackbarState = remember { SnackbarHostState() }
                val coroutineScope = rememberCoroutineScope()

                Scaffold(
                    bottomBar = { HomeBottomBar(navController) },
                    snackbarHost = { SnackbarHost(snackbarState) }
                ) { padding ->
                    NavGraph(
                        navController = navController,
                        startDestination = when {
                            !onboardingComplete -> MainDestinations.OnboardingRoute
                            userLoggedIn -> MainDestinations.HomeRoute
                            else -> MainDestinations.LoginRoute
                        },
                        padding = padding,
                        login = { startActivity(Intent(Intent.ACTION_VIEW, Api.OAuthUri)) },
                        showSnackbar = { coroutineScope.launch { snackbarState.showSnackbar(it) } }
                    )
                }
            }
        }
    }
}
