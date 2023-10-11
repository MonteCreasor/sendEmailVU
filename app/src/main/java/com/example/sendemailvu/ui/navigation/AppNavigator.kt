package com.example.sendemailvu.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController

/**
 * All application navigation routes (destinations) are declared here.
 * Each route is defined using a NavGraphBuilder extension function
 * (homeScreen, emailScreen, etc.) that defines the composable
 * screen to display for that route.
 */
@Composable
fun AppNavigator(
    startDestination: String,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Home Screen route (start destination) that uses the
        // passed lambda to navigate to the compose email screen.
        homeScreen(
            onNavigateToNewMessageScreen = {
                navController.navigateToNewMessage()
            }
        )

        // Compose Email Screen route is the final destination
        // and only supports back navigation which is automatically
        // handled by the parent NavAppScreen composable.
        emailScreen()
    }
}