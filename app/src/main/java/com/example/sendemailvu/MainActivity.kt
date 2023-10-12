package com.example.sendemailvu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import com.example.sendemailvu.ui.screens.AppBarScreen
import com.example.sendemailvu.ui.screens.EmailScreen
import com.example.sendemailvu.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // This call is required in order to use Modifier.imePadding()
        // in ComposeEmailScreen. Together they ensure that when
        // the user is typing text in the email body, the content
        // is automatically shifted up so that it will not be obscured
        // by the soft keyboard.
//        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            AppTheme {
                AppBarScreen()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainActivityPreview() {
    AppTheme {
        AppBarScreen()
    }
}