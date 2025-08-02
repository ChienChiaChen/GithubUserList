package com.example.githubuserlist.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class Screen(val route: String) {
    object UserList : Screen("user_list")
    
    object UserRepo : Screen("user_repo/{username}") {
        val arguments = listOf(
            navArgument("username") {
                type = NavType.StringType
            }
        )
        
        fun createRoute(username: String) = "user_repo/$username"
    }
} 