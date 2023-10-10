package com.example.sendemailvu.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.sendemailvu.R
import com.example.sendemailvu.ui.theme.AppTheme

@Composable
fun HomeScreen(composeEmailClicked: () -> Unit = {}) {
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Text(
            text = "Welcome to Vandy!",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .align(alignment = Alignment.TopCenter)
                .padding(16.dp)
        )

        Image(
            painter = painterResource(
                id = R.drawable.im_vu
            ),
            modifier = Modifier.fillMaxSize(),
            contentDescription = "Vanderbilt Logos"
        )
        Button(
            onClick = composeEmailClicked,
            modifier = Modifier
                .padding(16.dp)
                .align(alignment = Alignment.BottomCenter)
        ) {
            Text(text = "Compose Email")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainActivityPreview() {
    AppTheme {
        HomeScreen()
    }
}
