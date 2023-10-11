package com.example.sendemailvu.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.sendemailvu.ui.screens.EmailScreen

/**
 * Route name that is also used as the screen title
 * displayed in the AppBar.
 */
const val emailScreenRoute = "New Message"

/**
 * NavGraphBuilder extension function that defines the composable
 * for the compose email screen route.
 */
fun NavGraphBuilder.emailScreen() {
    composable(emailScreenRoute) {
        EmailScreen()
    }
}

/**
 * NavController extension function that navigates to the
 * compose email screen.
 */
fun NavController.navigateToNewMessage() {
    navigate(route = emailScreenRoute)
}