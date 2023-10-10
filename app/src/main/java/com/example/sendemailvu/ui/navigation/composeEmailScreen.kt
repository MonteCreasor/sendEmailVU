package com.example.sendemailvu.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.sendemailvu.ui.screens.ComposeEmailScreen

const val composeEmailScreenRoute = "New Message"

fun NavGraphBuilder.composeEmailScreen() {
    composable(composeEmailScreenRoute) {
        ComposeEmailScreen()
    }
}

fun NavController.navigateToNewMessage() {
    navigate(route = composeEmailScreenRoute)
}