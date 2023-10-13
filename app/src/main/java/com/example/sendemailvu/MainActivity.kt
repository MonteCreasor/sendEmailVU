package com.example.sendemailvu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.sendemailvu.ui.screens.EmailScreen
import com.example.sendemailvu.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                EmailScreen()
            }
        }
    }
}

@Preview
@Composable
fun PreviewEmailScreen() {
    AppTheme {
        EmailScreen()
    }
}