package com.example.humblr.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.humblr.ui.screens.onboarding.OnboardingScreen

object MainDestinations {

    const val OnboardingRoute = "onboarding"
//    const val LoginRoute = "login"
//    const val AuthRoute = "auth/{code}"
//    const val HomeRoute = "home"
//    const val SearchRoute = "search"
}

@Composable
fun NavGraph(
    startDestination: String
//    login: () -> Unit,
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(route = MainDestinations.OnboardingRoute) {
            OnboardingScreen {
//                navController.navigate(MainDestinations.LoginRoute) { popUpTo(0) }
            }
        }
    }
}
