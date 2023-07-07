package com.example.humblr.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navDeepLink
import com.example.humblr.ui.common.HomeTabs
import com.example.humblr.ui.common.SubredditsRoutes
import com.example.humblr.ui.screens.auth.AuthScreen
import com.example.humblr.ui.screens.favorite.FavoriteScreen
import com.example.humblr.ui.screens.login.LoginScreen
import com.example.humblr.ui.screens.onboarding.OnboardingScreen
import com.example.humblr.ui.screens.profile.ProfileScreen
import com.example.humblr.ui.screens.subreddit.SubredditScreen
import com.example.humblr.ui.screens.subreddits.SubredditsScreen
import com.example.humblr.util.RedirectUri

object MainDestinations {

    const val OnboardingRoute = "onboarding"
    const val LoginRoute = "login"
    const val AuthRoute = "auth/{query}"
    const val HomeRoute = "home"
}

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String,
    padding: PaddingValues,
    login: () -> Unit,
    showSnackbar: (String) -> Unit
) {
    NavHost(
        modifier = Modifier.padding(padding),
        navController = navController,
        startDestination = startDestination
    ) {
        composable(route = MainDestinations.OnboardingRoute) {
            OnboardingScreen {
                navController.navigate(MainDestinations.LoginRoute) { popUpTo(0) }
            }
        }
        composable(route = MainDestinations.LoginRoute) { LoginScreen(login) }
        composable(
            route = MainDestinations.AuthRoute,
            deepLinks = listOf(navDeepLink { uriPattern = "$RedirectUri#{query}" })
        ) {
            AuthScreen {
                navController.navigate(MainDestinations.HomeRoute) { popUpTo(0) }
            }
        }
        navigation(
            route = MainDestinations.HomeRoute,
            startDestination = HomeTabs.Subreddit.route
        ) {
            composable(route = HomeTabs.Subreddit.route) {
                SubredditsScreen(showSnackbar) {
                    navController.navigate(SubredditsRoutes.SubredditRoute + "/$it")
                }
            }
            composable(
                route = SubredditsRoutes.SubredditRoute + "/{${SubredditsRoutes.SubredditId}}"
            ) {
                SubredditScreen(showSnackbar, navController::navigateUp)
            }
            composable(route = HomeTabs.Favorite.route) {
                FavoriteScreen(showSnackbar)
            }
            composable(route = HomeTabs.Profile.route) {
                ProfileScreen(showSnackbar)
            }
        }
    }
}
