package com.example.sendemailvu.ui.screens

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.sendemailvu.ui.theme.AppTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ComposeEmailScreen() {
    var to by rememberSaveable { mutableStateOf("") }
    var subject by rememberSaveable { mutableStateOf("") }
    var message by rememberSaveable { mutableStateOf("") }
    val isKeyboardVisible by rememberUpdatedState(newValue = WindowInsets.isImeVisible)

    // Create a launcher for sending emails
    val sendEmailLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { _ ->
        // Handle the result if needed (not required in this example)
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        EmailTextField(
            value = to,
            onValueChange = { to = it },
            singleLine = true,
            prefix = { Text(text = "To:") }
        )

        EmailTextField(
            value = subject,
            onValueChange = { subject = it },
            singleLine = true,
            placeholder = { Text(text = "Subject") },
        )

        EmailTextField(
            value = message,
            onValueChange = { message = it },
            modifier = Modifier
                .weight(1f)
                .imePadding()
        )

        // For a nicer user experience, hide the send button when
        // the soft keyboard is visible; this will allow the email
        // body to expand to fill all the available space.
        if (!isKeyboardVisible) {
            Button(
                onClick = {
                    // Create an Intent object to perform a send action.
                    val sendIntent = Intent(Intent.ACTION_SEND).apply {
                        // We are sending the text contents of each
                        // TextField, so set "plain/text" MIME type.
                        type = "plain/text"

                        // Insert the email addresses, subject and body
                        // as Intent's extras (only 1 email address is
                        // currently supported).
                        putExtra(Intent.EXTRA_EMAIL, arrayOf(to))
                        putExtra(Intent.EXTRA_SUBJECT, subject)
                        putExtra(Intent.EXTRA_TEXT, message)
                    }

                    // Launch the Intent using the previously created launcher.
                    // The createChooser method allows the user to select from
                    // multiple email client options if available.
                    sendEmailLauncher.launch(
                        Intent.createChooser(sendIntent, "Send Email")
                    )
                },
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(text = "Send")
                    Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.Send,
                        contentDescription = "Send Email",
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
            }
        }
    }
}

/**
 * A custom TextField that is used to hide some of the
 * complexity required to support each email TextField.
 */
@Composable
fun EmailTextField(
    value: String,
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    onValueChange: (String) -> Unit,
    singleLine: Boolean = false,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    TextField(
        value = value,
        onValueChange = {
            onValueChange(it)
        },
        modifier = modifier.fillMaxWidth(),
        label = label,
        placeholder = placeholder,
        singleLine = singleLine,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedIndicatorColor = Color.LightGray,
            unfocusedIndicatorColor = Color.LightGray,
        ),
        keyboardActions = KeyboardActions(
            onDone = { keyboardController?.hide() }
        ),
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = if (singleLine) ImeAction.Done else ImeAction.Default
        ),
        prefix = prefix
    )
}

@Preview(showBackground = true)
@Composable
fun NewMessagePreview() {
    AppTheme {
        ComposeEmailScreen()
    }
}