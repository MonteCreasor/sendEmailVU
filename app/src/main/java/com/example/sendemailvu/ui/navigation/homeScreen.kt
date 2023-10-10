package com.example.sendemailvu.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.sendemailvu.ui.screens.HomeScreen

const val homeScreenRoute = "Send Email Example"

fun NavGraphBuilder.homeScreen(
    onNavigateToNewMessageScreen: () -> Unit
) {
    composable(homeScreenRoute) {
        HomeScreen(composeEmailClicked = onNavigateToNewMessageScreen)
    }
}

fun NavController.navigateToHome() {
    navigate(route = homeScreenRoute) {
        popUpTo(0) {
            inclusive = true
        }
        launchSingleTop = true
    }
}