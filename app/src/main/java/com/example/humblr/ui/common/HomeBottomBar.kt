package com.example.humblr.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.humblr.ui.MainDestinations
import com.example.humblr.ui.theme.Palette

@Composable
fun HomeBottomBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: return

    if (!currentRoute.contains(MainDestinations.HomeRoute)) return

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        color = Palette.current.primary
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            HomeTabs.values().forEach {
                Icon(
                    painter = painterResource(it.icon),
                    contentDescription = null,
                    tint = if (currentRoute.contains(it.route)) Color.White else Color(0xFF764BE9),
                    modifier = Modifier
                        .size(40.dp)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            if (!currentRoute.contains(it.route)) {
                                navController.navigate(it.route) {
                                    popUpTo(MainDestinations.HomeRoute) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                )
            }
        }
    }
}
