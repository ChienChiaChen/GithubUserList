package com.example.githubuserlist.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.githubuserlist.ui.screens.UserListScreen
import com.example.githubuserlist.ui.screens.UserRepoScreen

@Composable
fun AppNavigation(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Screen.UserList.route
    ) {
        composable(Screen.UserList.route) {
            UserListScreen(
                onUserClick = { username ->
                    navController.navigate(Screen.UserRepo.createRoute(username))
                }
            )
        }
        
        composable(
            route = Screen.UserRepo.route,
            arguments = Screen.UserRepo.arguments
        ) { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: ""
            UserRepoScreen(
                username = username,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
} 