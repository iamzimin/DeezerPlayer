package com.evg.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.evg.resource.R
import com.evg.ui.extensions.makeTransparent
import com.evg.ui.theme.AppTheme
import com.evg.ui.theme.DeezerPlayerTheme
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.drop

@OptIn(FlowPreview::class)
@Composable
fun SearchTextField(
    modifier: Modifier = Modifier,
    onTextChangeDebounced: (text: String) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val keyboardState by keyboardAsState()
    var typedText by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue("")) }

    LaunchedEffect(keyboardState) {
        if (keyboardState == Keyboard.Closed) {
            focusManager.clearFocus()
        }
    }

    LaunchedEffect(Unit) {
        snapshotFlow { typedText.text }
            .drop(1)
            .debounce(500)
            .collect { debouncedText ->
                onTextChangeDebounced(debouncedText)
            }
    }

    OutlinedTextField(
        value = typedText,
        onValueChange = { newText ->
            typedText = newText
        },
        label = { Text(text = stringResource(R.string.track_search)) },
        modifier = modifier
            .fillMaxWidth()
            .height(70.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors().copy(
            focusedTextColor = AppTheme.colors.text,
            unfocusedTextColor = AppTheme.colors.text,
            cursorColor = AppTheme.colors.primary,
            focusedIndicatorColor = AppTheme.colors.primary,
            unfocusedIndicatorColor = AppTheme.colors.primary,
            focusedLabelColor = AppTheme.colors.primary,
            unfocusedLabelColor = AppTheme.colors.primary,
            focusedContainerColor = AppTheme.colors.textFieldBackground,
            unfocusedContainerColor = AppTheme.colors.textFieldBackground,
        )
    )
}

@Composable
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
fun SearchTextFieldPreview(darkTheme: Boolean = true) {
    DeezerPlayerTheme(darkTheme = darkTheme) {
        Surface(color = AppTheme.colors.background) {
            SearchTextField(
                onTextChangeDebounced = {},
            )
        }
    }
}