/*
 * Copyright 2022 Lynn Sakashita
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hisui.kanna.feature.book

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.hisui.kanna.core.common.util.dateTimeFormatter
import com.hisui.kanna.core.designsystem.theme.KannaTheme
import com.hisui.kanna.core.model.NewBook
import kotlinx.datetime.Clock
import kotlinx.datetime.toJavaInstant

@Composable
internal fun NewBookRoute(
    viewModel: NewBookViewModel = hiltViewModel(),
    isCompactScreen: Boolean,
    popBackStack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    NewBookDialog(
        isCompactScreen = isCompactScreen,
        onDismiss = popBackStack
    ) {
        NewBookScreen(
            isCompactScreen = isCompactScreen,
            uiState = uiState,
            popBackStack = popBackStack,
            onUpdateBook = viewModel::updateBook,
        )
    }
}

@Composable
private fun NewBookDialog(
    isCompactScreen: Boolean,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit,
) {
    Dialog(
        properties = DialogProperties(
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = !isCompactScreen
        ),
        onDismissRequest = onDismiss
    ) {
        content()
    }
}

@Preview(device = Devices.PIXEL_4) @Composable
private fun NewBookDialogCompactPreview() { NewBookScreenPreviewBase(isCompactScreen = true) }

@Preview(device = Devices.TABLET) @Composable
private fun NewBookDialogTabletPreview() { NewBookScreenPreviewBase(isCompactScreen = false) }

@Preview(device = Devices.DESKTOP) @Composable
private fun NewBookDialogDesktopPreview() { NewBookScreenPreviewBase(isCompactScreen = false) }

/**
 * This screen is shown as a full screen on `compact` devices
 * and as a dialog on `medium` & `large` devices.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun NewBookScreen(
    isCompactScreen: Boolean,
    uiState: NewBookUiState,
    popBackStack: () -> Unit,
    onUpdateBook: (NewBook) -> Unit
) {
    Scaffold(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(if (isCompactScreen) 1f else 0.65f),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.new_book)) },
                navigationIcon = {
                    IconButton(onClick = popBackStack) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = stringResource(id = com.hisui.kanna.core.ui.R.string.close)
                        )
                    }
                },
                actions = {
                    Button(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        onClick = { },
                    ) {
                        Text(stringResource(id = com.hisui.kanna.core.ui.R.string.save))
                    }
                }
            )
        }
    ) { paddingValues ->
        val newBook = uiState.newBook
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = newBook.title,
                onValueChange = { onUpdateBook(newBook.copy(title = it)) },
                label = { Text(text = stringResource(id = R.string.title)) }
            )

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = dateTimeFormatter.format(newBook.readDate.toJavaInstant()),
                onValueChange = { /* TODO */ },
                label = { Text(text = stringResource(id = R.string.read_date)) },
                readOnly = true,
            )

            BookFormDivider()

            BookRating(
                value = newBook.rating,
                onUpdate = { rating -> onUpdateBook(newBook.copy(rating = rating)) }
            )

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                value = newBook.thought,
                onValueChange = { onUpdateBook(newBook.copy(thought = it)) },
                label = { Text(text = stringResource(id = R.string.thought)) },
            )

            BookFormDivider()

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = newBook.thought,
                onValueChange = { onUpdateBook(newBook.copy(memo = it)) },
                label = { Text(text = stringResource(id = R.string.memo)) },
            )
        }
    }
}

@Preview
@Composable
private fun NewBookScreen() {
    KannaTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            NewBookScreen(
                isCompactScreen = false,
                uiState = previewUiState,
                popBackStack = {},
                onUpdateBook = {},
            )
        }
    }
}

@Composable
private fun BookRating(
    value: Int,
    onUpdate: (rating: Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            modifier = Modifier.padding(bottom = 16.dp),
            text = stringResource(id = R.string.rating),
            style = MaterialTheme.typography.headlineSmall
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(5) { index ->
                val coloured = value > index
                val tint by animateColorAsState(
                    targetValue = if (coloured) Color(0xFFFFDF00) else MaterialTheme.colorScheme.surfaceVariant
                )
                Icon(
                    modifier = Modifier
                        .size(48.dp)
                        .clickable { onUpdate(index + 1) },
                    imageVector = if (coloured) Icons.Filled.Grade else Icons.Filled.StarOutline,
                    contentDescription = null,
                    tint = tint,
                )
            }
        }
    }
}

@Composable
private fun BookFormDivider() {
    Divider(
        modifier = Modifier.padding(top = 16.dp),
        thickness = 1.dp,
        color = MaterialTheme.colorScheme.surfaceVariant
    )
}

private val previewUiState: NewBookUiState =
    NewBookUiState(
        loading = false,
        error = null,
        newBook = NewBook(
            title = "title",
            readDate = Clock.System.now(),
            thought = "",
            memo = "",
            rating = 3,
            authorId = "author",
            genreId = "genre"
        ),
        authors = emptyList(),
        genres = emptyList()
    )

@Composable
private fun NewBookScreenPreviewBase(isCompactScreen: Boolean) {
    KannaTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            NewBookDialog(
                isCompactScreen = isCompactScreen,
                onDismiss = {}
            ) {
                NewBookScreen(
                    isCompactScreen = isCompactScreen,
                    uiState = previewUiState,
                    onUpdateBook = {},
                    popBackStack = {}
                )
            }
        }
    }
}
