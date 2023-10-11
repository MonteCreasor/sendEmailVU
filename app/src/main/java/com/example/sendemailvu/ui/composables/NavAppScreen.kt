package com.example.sendemailvu.ui.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.sendemailvu.ui.screens.EmailScreen
import com.example.sendemailvu.ui.screens.HomeScreen
import com.example.sendemailvu.ui.theme.AppTheme

/**
 * Screen route names that are also used as the screen title
 * displayed in the AppBar.
 */
const val emailScreenRoute = "New Message"
const val homeScreenRoute = "Send Email Example"

/**
 * A generic reusable screen that includes an app bar with title
 * along with navigation support that automatically handles
 * back navigation support. The [AppNavigator] declared in the
 * Scaffold body contains the application's custom navigation
 * destination routes (graph).
 */
@Composable
fun NavAppScreen(
    navController: NavHostController = rememberNavController(),
    startDestination: String
) {
    val backStackEntry: NavBackStackEntry? by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: startDestination

    Scaffold(
        topBar = {
            NavTopBar(
                title = currentRoute,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.popBackStack() }
            )
        },
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(paddingValues)
        ) {
            // Home Screen route (start destination) that uses the
            // passed lambda to navigate to the compose email screen.
            composable(homeScreenRoute) {
                HomeScreen(sendButtonClicked = {
                    navController.navigate(route = emailScreenRoute)
                })
            }

            // Compose Email Screen route is the final destination
            // and only supports back navigation which is automatically
            // handled by the parent NavAppScreen composable.
            composable(emailScreenRoute) {
                EmailScreen()
            }
        }
    }
}

/**
 * A generic top app bar that includes a title and
 * automatic back navigation support.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavTopBar(
    title: String,
    canNavigateBack: Boolean = false,
    navigateUp: () -> Unit
) {
    TopAppBar(
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = { navigateUp() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                    )
                }
            }
        },
        title = {
            Text(title)
        },
    )
}

@Preview
@Composable
fun NavAppScreenPreview() {
    AppTheme {
        NavAppScreen(
            startDestination = homeScreenRoute
        )
    }
}