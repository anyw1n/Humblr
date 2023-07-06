package com.example.humblr.ui.common

import androidx.annotation.DrawableRes
import com.example.humblr.R
import com.example.humblr.ui.MainDestinations

enum class HomeTabs(@DrawableRes val icon: Int, val route: String) {
    Subreddit(R.drawable.subreddit, HomeDestinations.SubredditsRoute),
    Favorite(R.drawable.favorite, HomeDestinations.FavoriteRoute),
    Profile(R.drawable.app_icon, HomeDestinations.ProfileRoute)
}

object HomeDestinations {
    const val SubredditsRoute = MainDestinations.HomeRoute + "/subreddits"
    const val FavoriteRoute = MainDestinations.HomeRoute + "/favorite"
    const val ProfileRoute = MainDestinations.HomeRoute + "/profile"
}

object SubredditsRoutes {
    const val SubredditRoute = HomeDestinations.SubredditsRoute + "/subreddit"
    const val SubredditId = "subredditId"
}
