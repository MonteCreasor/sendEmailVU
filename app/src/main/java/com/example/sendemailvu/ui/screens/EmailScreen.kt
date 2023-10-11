package com.example.sendemailvu.ui.screens

import android.content.Intent
import android.widget.Toast
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
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.sendemailvu.ui.theme.AppTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EmailScreen() {
    // User rememberSaveable for all text fields so that their
    // contents survive configuration changes (e.g. rotations).
    var to by rememberSaveable { mutableStateOf("") }
    var subject by rememberSaveable { mutableStateOf("") }
    var message by rememberSaveable { mutableStateOf("") }

    val isValidEmail by rememberUpdatedState(newValue = isValidEmail(to))

    // We need to know if the keyboard is visible so that we can adjust
    // the email body text field to always display above the keyboard
    // and never beneath it.
    val isKeyboardVisible by rememberUpdatedState(newValue = WindowInsets.isImeVisible)

    // A focus request is used to ensure that the focus is on the To
    // text field when the screen is first displayed.
    val focusRequester = remember { FocusRequester() }

    // A context is needed for the send button click handler to call startActivity.
    val context = LocalContext.current

    // Used a launch effect to set focus to the To field when
    // the screen is first displayed.
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // The To text field is a single line and supports multiple
        // emails that must be comma separated. This field also
        // does a validation check to ensure that any entered values
        // are valid email addresses.
        EmailTextField(value = to,
            onValueChange = {
                to = it
            },
            // Sets initial focus to the this field.
            modifier = Modifier.focusRequester(focusRequester),
            isValid = isValidEmail,
            supportingText = "Invalid email address",
            singleLine = true,
            prefix = { Text(text = "To:") })

        // The Subject text field is a single line and is not required.
        EmailTextField(
            value = subject,
            onValueChange = { subject = it },
            singleLine = true,
            placeholder = { Text(text = "Subject") },
        )

        // The Body text field is multiline and is not required.
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
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        // Directly name the standard Gmail package as the
                        // target of this request so that an app chooser is
                        // not required.
                        setPackage("com.google.android.gm")

                        // We are sending plain text Intent extras.
                        type = "plain/text"

                        // Insert the email addresses, subject, and body
                        // as Intent's extras. Email addresses must be
                        // separated by commas.
                        putExtra(
                            Intent.EXTRA_EMAIL,
                            to.split(",")
                                .map { it.trim() }
                                .toTypedArray()
                        )
                        putExtra(Intent.EXTRA_SUBJECT, subject)
                        putExtra(Intent.EXTRA_TEXT, message)
                    }

                    // Ensure that the declared gmail package can be
                    // resolved (found on the device) and if found,
                    // then start send the intent to start the Compose
                    // activity that will populate its fields with the
                    // with the Intent extras.
                    if (intent.resolveActivity(context.packageManager) != null) {
                        context.startActivity(intent)
                    } else {
                        Toast.makeText(
                            context,
                            "GMail client not found.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                modifier = Modifier.padding(16.dp),
                enabled = isValidEmail
            )
        }
    }
}

/**
 * Helper to ensure that passed [addresses] string contains
 * a comma separated list of valid email addresses.
 */
internal fun isValidEmail(addresses: String): Boolean {
    // A regular expression to ensure that the gmail address is valid.
    val emailRegex = """[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}""".toRegex()

    return addresses.split(",")
        .map { it.trim() }
        .filterNot { it.matches(emailRegex) }
        .isEmpty()
}

/**
 * A custom TextField that is used to hide some of the
 * complexity required to support error indicators for
 * TextFields.
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
    singleLine: Boolean = false,
    isValid: Boolean = true,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var showSupportingText by remember { mutableStateOf(false) }
    var isFocused by remember { mutableStateOf(false) }

    // Show error indicator when field value is invalid and
    // user is not actively editing the field to fix the issue.
    val isError by rememberUpdatedState(newValue = !isValid && !isFocused)

    TextField(
        value = value,
        onValueChange = {
            onValueChange(it)
        },
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged {
                isFocused = it.isFocused
                showSupportingText = !isFocused && !isValid
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
        supportingText = if (!isValid && showSupportingText) {
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