package com.example.sendemailvu.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.sendemailvu.ui.screens.ComposeEmailScreen

/**
 * Route name that is also used as the screen title
 * displayed in the AppBar.
 */
const val composeEmailScreenRoute = "New Message"

/**
 * NavGraphBuilder extension function that defines the composable
 * for the compose email screen route.
 */
fun NavGraphBuilder.composeEmailScreen() {
    composable(composeEmailScreenRoute) {
        ComposeEmailScreen()
    }
}

/**
 * NavController extension function that navigates to the
 * compose email screen.
 */
fun NavController.navigateToNewMessage() {
    navigate(route = composeEmailScreenRoute)
}