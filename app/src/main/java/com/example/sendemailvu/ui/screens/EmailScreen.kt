package com.example.sendemailvu.ui.screens

import android.content.Context
import android.content.Intent
import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
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
fun EmailScreen(
    titleId: Int = R.string.app_name
) {
    // The screen app bar title.
    val title = stringResource(id = titleId)

    // Support scrolling if needed.
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(
        rememberTopAppBarState()
    )

    // A context is needed for starting the GMail activity.
    val context = LocalContext.current

    // User rememberSaveable for all text fields so that their
    // contents survive configuration changes (e.g. rotations).
    val to = rememberSaveable { mutableStateOf("") }
    val subject = rememberSaveable { mutableStateOf("") }
    val message = rememberSaveable { mutableStateOf("") }
    val attachments = remember { mutableStateListOf<Uri>() }

    // Initialize the activity result launcher that will be used
    // to launch the file picker activity.
    val filePickerLauncher: ManagedActivityResultLauncher<Array<String>, Uri?> =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            if (uri != null) {
                attachments.add(uri)
            }
        }

    // This Scaffold is used to provide a top app bar with attach and send action buttons.
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text(text = title) },
                scrollBehavior = scrollBehavior,
                actions = {
                    // Attach action starts a file picker.
                    IconButton(
                        onClick = { filePickerLauncher.launch(arrayOf("*/*")) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.AttachFile,
                            contentDescription = "Attach File",
                            modifier = Modifier.rotate(90f),
                        )
                    }

                    // Send action calls the sendEmail helper function
                    // to send email contents and possible attachments.
                    IconButton(
                        onClick = {
                            sendEmail(
                                context, to = to.value,
                                subject = subject.value,
                                message = message.value,
                                attachments = attachments
                            )
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.Send,
                            contentDescription = "Send Email",
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
        ) {
            EmailContent(
                to = to,
                subject = subject,
                message = message,
                attachments = attachments,
                modifier = Modifier,
            )
        }
    }
}

@Composable
fun EmailContent(
    to: MutableState<String>,
    subject: MutableState<String>,
    message: MutableState<String>,
    attachments: MutableList<Uri>,
    modifier: Modifier
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

    // Support scrolling if needed.
    val scrollState = rememberScrollState()

    var bodyHasFocus by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
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

        EmailTextField(
            value = subject.value,
            onValueChange = {
                subject.value = it
            },
            singleLine = true,
            placeholder = { Text(text = "Subject") },
        )

        EmailTextField(
            value = message.value,
            onValueChange = {
                message.value = it
            },
            modifier = Modifier
                .imePadding()
                .fillMaxWidth()
                .onFocusChanged {
                    bodyHasFocus = it.isFocused
                },
            hideUnderline = true,
        )

        // Display attachments optional attachments.
        if (attachments.isNotEmpty()) {
            Attachments(
                attachments = attachments,
                onCloseClicked = {
                    attachments.removeAt(it)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
        }
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
    hideUnderline: Boolean = false,
    isValid: Boolean = true,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var showSupportingText by remember { mutableStateOf(false) }
    var isFocused by remember { mutableStateOf(false) }

    // Show error indicator when field value is invalid and
    // user is not actively editing the field to fix the issue.
    val isError by rememberUpdatedState(newValue = !isValid && !isFocused)

    val indicatorColor = if (hideUnderline) {
        Color.Transparent
    } else {
        MaterialTheme.colorScheme.outlineVariant
    }

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
            focusedIndicatorColor = indicatorColor,
            unfocusedIndicatorColor = indicatorColor,
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
 * A composable that displays a list of attachments.
 */
@Composable
fun Attachments(
    attachments: List<Uri>,
    onCloseClicked: (index: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start

    ) {
        attachments.forEachIndexed { index, uri ->
            AttachmentItem(
                uri = uri,
                onCloseClicked = { onCloseClicked(index) }
            )
        }
    }
}

/**
 * A composable that displays a single attachment.
 */
@Composable
fun AttachmentItem(uri: Uri, onCloseClicked: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .clip(RoundedCornerShape(4.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Default.InsertDriveFile,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.surfaceTint,
            modifier = Modifier.size(32.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = getDisplayNameFromUri(LocalContext.current, uri) ?: "Unknown",
            modifier = Modifier.weight(1f),
            maxLines = 1,
        )

        IconButton(
            onClick = onCloseClicked
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Send Email",
            )
        }
    }
}

/**
 * Helper to get the a human readable display name for a given [uri].
 */
fun getDisplayNameFromUri(context: Context, uri: Uri): String? {
    var displayName: String? = null
    context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (cursor.moveToFirst()) {
            displayName = cursor.getString(nameIndex)
        }
    }
    return displayName
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
fun sendEmail(
    context: Context,
    to: String,
    subject: String,
    message: String,
    attachments: List<Uri>
) {
    // Create an Intent object to perform a send action.
    val intent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
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

        if (subject.isNotEmpty()) {
            putExtra(Intent.EXTRA_SUBJECT, subject)
        }

        if (message.isNotEmpty()) {
            putExtra(Intent.EXTRA_TEXT, message)
        }

        if (attachments.isNotEmpty()) {
            putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(attachments))
        }
    }

    context.startActivity(Intent.createChooser(intent, "Send with..."))
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO)
@Composable
fun EmailScreenPreview() {
    AppTheme {
        EmailScreen()
    }
}