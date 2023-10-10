package com.example.sendemailvu.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.sendemailvu.ui.screens.HomeScreen

/**
 * Route name that is also used as the screen title
 * displayed in the AppBar.
 */
const val homeScreenRoute = "Send Email Example"

/**
 * NavGraphBuilder extension function that defines the composable
 * for the home screen route.
 */
fun NavGraphBuilder.homeScreen(
    onNavigateToNewMessageScreen: () -> Unit
) {
    composable(homeScreenRoute) {
        HomeScreen(composeEmailClicked = onNavigateToNewMessageScreen)
    }
}

/**
 * NavController extension function that navigates to the home
 * screen and pops all other screens from the back stack.
 * (Not used in the example app)
 */
fun NavController.navigateToHome() {
    navigate(route = homeScreenRoute) {
        popUpTo(0) {
            inclusive = true
        }
        launchSingleTop = true
    }
}