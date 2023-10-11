package com.example.sendemailvu.ui.screens

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.sendemailvu.ui.theme.AppTheme


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EmailScreen() {
    val emailRegex = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}".toRegex()
    var to by rememberSaveable { mutableStateOf("") }
    var subject by rememberSaveable { mutableStateOf("") }
    var message by rememberSaveable { mutableStateOf("") }
    val isKeyboardVisible by rememberUpdatedState(newValue = WindowInsets.isImeVisible)
    val isValidEmail by rememberUpdatedState(newValue = to.matches(emailRegex))
    val focusRequester = remember { FocusRequester() }

    // Create a launcher for sending emails
    val sendEmailLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { _ -> }

    // Used a launch effect to set focus to the To field when
    // the screen is first displayed.
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(
        modifier = Modifier.fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        EmailTextField(value = to,
            onValueChange = {
                to = it
            },
            modifier = Modifier.focusRequester(focusRequester),
            isValid = { it.matches(emailRegex) },
            supportingText = "Invalid email address",
            singleLine = true,
            prefix = { Text(text = "To:") })

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
                .imePadding()
                .weight(1f)
        )

        // For a nicer user experience, don't show the send button
        // when the soft keyboard is visible. This allows the email
        // body TextField to use the entire screen area above the
        // soft keyboard.
        if (!isKeyboardVisible) {
            SendButton(
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
                modifier = Modifier.padding(16.dp),
                enabled = isValidEmail
            )
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
    supportingText: String? = null,
    onValueChange: (String) -> Unit,
    isValid: (String) -> Boolean = { true },
    singleLine: Boolean = false,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var showSupportingText by remember { mutableStateOf(false) }
    var isFocused by remember { mutableStateOf(false) }

    // Show error indicator when field value is invalid and
    // user is not actively editing the field to fix the issue.
    val isError by rememberUpdatedState(newValue = !isValid(value) && !isFocused)

    TextField(
        value = value,
        onValueChange = {
            onValueChange(it)
        },
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged {
                isFocused = it.isFocused
                showSupportingText = !isFocused && !isValid(value)
            },
        label = label,
        placeholder = placeholder,
        singleLine = singleLine,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedIndicatorColor = Color.LightGray,
            unfocusedIndicatorColor = Color.LightGray,
        ),
        keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = if (singleLine) ImeAction.Done else ImeAction.Default
        ),
        prefix = prefix,
        supportingText = if (!isValid(value) && showSupportingText) {
            { Text(text = supportingText ?: "Invalid") }
        } else {
            null
        },
        isError = isError,
    )
}

/**
 * A custom send Button that shows both text and an icon.
 */
@Composable
fun SendButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick, modifier = modifier, enabled = enabled
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

@Preview(showBackground = true)
@Composable
fun NewMessagePreview() {
    AppTheme {
        EmailScreen()
    }
}