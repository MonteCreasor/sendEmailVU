package com.example.sendemailvu.ui.screens

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.sendemailvu.R
import com.example.sendemailvu.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBarScreen(
    titleId: Int = R.string.app_name,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    // User rememberSaveable for all text fields so that their
    // contents survive configuration changes (e.g. rotations).
    val to = rememberSaveable { mutableStateOf("") }
    val subject = rememberSaveable { mutableStateOf("") }
    val message = rememberSaveable { mutableStateOf("") }

    // A context is needed for starting the GMail activity.
    val context = LocalContext.current

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(
        rememberTopAppBarState()
    )

    val title = stringResource(id = titleId)

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text(text = title) },
                scrollBehavior = scrollBehavior,
                actions = {
                    IconButton(
                        onClick = {
                            /* TODO */
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.AttachFile,
                            contentDescription = "Send Email",
                        )
                    }
                    IconButton(
                        onClick = {
                            sendEmail(
                                context, to = to.value,
                                subject = subject.value,
                                message = message.value
                            )
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.Send,
                            contentDescription = "Send Email",
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            EmailScreen(
                to = to,
                subject = subject,
                message = message
            )
        }
    }
}

@Composable
fun EmailScreen(
    to: MutableState<String>,
    subject: MutableState<String>,
    message: MutableState<String>
) {
    // A remembered state to keep track of whether the entered is valid.
    val isValidEmail by rememberUpdatedState(newValue = isValidEmail(to.value))

    // A focus request is used to ensure that the focus is on the To
    // text field when the screen is first displayed.
    val focusRequester = remember { FocusRequester() }

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
        EmailTextField(
            value = to.value,
            onValueChange = {
                to.value = it
            },
            // Sets initial focus to the this field.
            modifier = Modifier.focusRequester(focusRequester),
            isValid = isValidEmail,
            supportingText = "Invalid email address",
            singleLine = true,
            prefix = {
                Text(
                    text = "To",
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        )

        // The Subject text field is a single line and is not required.
        EmailTextField(
            value = subject.value,
            onValueChange = {
                subject.value = it
            },
            singleLine = true,
            placeholder = { Text(text = "Subject") },
        )

        // The Body text field is multiline and is not required.
        EmailTextField(
            value = message.value,
            onValueChange = {
                message.value = it
            },
            modifier = Modifier
                .weight(1f)
        )
    }
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
            focusedContainerColor = MaterialTheme.colorScheme.background,
            unfocusedContainerColor = MaterialTheme.colorScheme.background,
            focusedIndicatorColor = MaterialTheme.colorScheme.outlineVariant,
            unfocusedIndicatorColor = MaterialTheme.colorScheme.outlineVariant
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
 * Helper to send an email using the Gmail app.
 */
fun sendEmail(context: Context, to: String, subject: String?, message: String?) {
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
        subject?.let { putExtra(Intent.EXTRA_SUBJECT, subject) }
        message?.let { putExtra(Intent.EXTRA_TEXT, message) }
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
}

@Preview(showBackground = true)
@Composable
fun NewMessagePreview() {
    val to = rememberSaveable { mutableStateOf("test@gmail.com") }
    val subject = rememberSaveable { mutableStateOf("Welcome!") }
    val message = rememberSaveable { mutableStateOf("This is a GMail example app") }
    AppTheme {
        EmailScreen(to, subject, message)
    }
}