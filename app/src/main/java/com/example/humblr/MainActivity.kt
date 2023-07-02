package com.example.humblr

import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.humblr.ui.MainDestinations
import com.example.humblr.ui.NavGraph
import com.example.humblr.ui.theme.HumblrTheme
import com.example.humblr.util.OnboardingCompleteKey
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val onboardingComplete = prefs.getBoolean(OnboardingCompleteKey, false)
        setContent {
            HumblrTheme {
                NavGraph(
                    startDestination = MainDestinations.OnboardingRoute
//                    when {
//                        !onboardingComplete -> MainDestinations.OnboardingRoute
//                        userLoggedIn -> MainDestinations.HomeRoute
//                        else -> MainDestinations.LoginRoute
//                    },
//                    login = { startActivity(Intent(Intent.ACTION_VIEW, Api.OAuthUri)) },
                )
            }
        }
    }
}
