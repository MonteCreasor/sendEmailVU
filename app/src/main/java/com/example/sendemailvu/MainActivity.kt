package com.example.sendemailvu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.sendemailvu.ui.composables.NavAppScreen
import com.example.sendemailvu.ui.navigation.homeScreenRoute
import com.example.sendemailvu.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                NavAppScreen(startDestination = homeScreenRoute)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainActivityPreview() {
    AppTheme {
        NavAppScreen(startDestination = homeScreenRoute)
    }
}