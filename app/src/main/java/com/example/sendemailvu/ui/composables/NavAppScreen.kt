package com.example.sendemailvu.ui.composables

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.sendemailvu.ui.navigation.AppNavigator
import com.example.sendemailvu.ui.navigation.homeScreenRoute
import com.example.sendemailvu.ui.theme.AppTheme

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
    val currentRoute = currentRoute(backStackEntry = backStackEntry) ?: startDestination

    Scaffold(
        topBar = {
            NavTopBar(
                title = currentRoute,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.popBackStack() }
            )
        },
    ) { paddingValues ->
        AppNavigator(
            startDestination = startDestination,
            modifier = Modifier.padding(paddingValues),
            navController = navController
        )
    }
}

/**
 * Strips optional query parameters from the route.
 */
fun currentRoute(backStackEntry: NavBackStackEntry?): String? {
    return backStackEntry?.destination?.route?.substringBefore("?")
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