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

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.hisui.kanna.core.designsystem.theme.KannaTheme
import com.hisui.kanna.core.model.Author
import com.hisui.kanna.core.model.Book
import com.hisui.kanna.core.model.BookReadStatus
import com.hisui.kanna.core.model.NewBook
import com.hisui.kanna.core.ui.component.CreateFormTopBar
import com.hisui.kanna.core.ui.preview.PreviewColumnWrapper
import com.hisui.kanna.core.ui.util.dateTimeFormatter
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant

@Composable
internal fun NewBookRoute(
    viewModel: NewBookViewModel = hiltViewModel(),
    isWidthCompact: Boolean,
    isHeightCompact: Boolean,
    popBackStack: () -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                NewBookEvent.Created -> popBackStack()
            }
        }
    }

    val uiState by viewModel.uiState.collectAsState()

    NewBookDialog(
        isWidthCompact = isWidthCompact,
        onDismiss = popBackStack
    ) {
        NewBookScreen(
            isCompact = isWidthCompact || isHeightCompact,
            uiState = uiState,
            popBackStack = popBackStack,
            onUpdateBook = viewModel::updateBook,
            onSelectStatus = viewModel::selectStatus,
            onSelectAuthor = viewModel::selectAuthor,
            onSelectGenre = viewModel::selectGenre,
            onShowDatePicker = viewModel::showDatePicker,
            onCreateBook = viewModel::createBook
        )
    }

    if (uiState.showDatePicker) {
        ReadDatePicker(
            onDismiss = viewModel::dismissDatePicker,
            onUpdate = { viewModel.updateBook(book = uiState.newBook.copy(readDate = it)) }
        )
    }
}

@Composable
private fun NewBookDialog(
    isWidthCompact: Boolean,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    Dialog(
        properties = DialogProperties(
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = !isWidthCompact
        ),
        onDismissRequest = onDismiss
    ) {
        content()
    }
}

@Preview(device = Devices.PIXEL_4)
@Composable
private fun NewBookDialogCompactPreview() { NewBookScreenPreviewBase(isCompactScreen = true) }

@Preview(device = Devices.TABLET)
@Composable
private fun NewBookDialogTabletPreview() { NewBookScreenPreviewBase(isCompactScreen = false) }

@Preview(device = Devices.DESKTOP)
@Composable
private fun NewBookDialogDesktopPreview() { NewBookScreenPreviewBase(isCompactScreen = false) }

@Composable
private fun SectionHeader(
    modifier: Modifier = Modifier,
    title: String
) {
    Text(
        modifier = modifier.padding(bottom = 16.dp),
        text = title,
        style = MaterialTheme.typography.headlineSmall
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReadDatePicker(
    onDismiss: () -> Unit,
    onUpdate: (Instant) -> Unit
) {
    val datePickerState = rememberDatePickerState()
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { epochMilli ->
                        onUpdate(Instant.fromEpochMilliseconds(epochMilli))
                    }
                }
            ) {
                Text(text = stringResource(id = com.hisui.kanna.core.ui.R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(id = com.hisui.kanna.core.ui.R.string.cancel))
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

/**
 * This screen is shown as a full screen on `compact` devices
 * and as a dialog on `medium` & `large` devices.
 */
@Composable
internal fun NewBookScreen(
    isCompact: Boolean,
    uiState: NewBookUiState,
    popBackStack: () -> Unit,
    onUpdateBook: (NewBook) -> Unit,
    onSelectStatus: (BookReadStatus) -> Unit,
    onSelectAuthor: (Author) -> Unit,
    onSelectGenre: (String) -> Unit,
    onShowDatePicker: () -> Unit,
    onCreateBook: (NewBook) -> Unit
) {
    Scaffold(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(if (isCompact) 1f else 0.65f),
        topBar = {
            CreateFormTopBar(
                title = stringResource(id = R.string.new_book),
                onClickNavigationIcon = popBackStack,
                onCreate = { onCreateBook(uiState.newBook) }
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
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = newBook.title,
                onValueChange = { onUpdateBook(newBook.copy(title = it)) },
                label = { Text(text = stringResource(id = R.string.title)) },
                keyboardOptions = KeyboardOptions(KeyboardCapitalization.Sentences)
            )

            StatusSelection(
                currentStatus = uiState.selectedStatus,
                readStatuses = uiState.statuses,
                onSelect = onSelectStatus
            )

            AnimatedVisibility(visible = uiState.selectedStatus == Book.Status.HAVE_READ) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(
                            modifier = Modifier.clickable { onShowDatePicker() },
                            imageVector = Icons.Filled.EditCalendar,
                            contentDescription = "Calendar"
                        )
                    },
                    value = dateTimeFormatter().format(newBook.readDate.toJavaInstant()),
                    onValueChange = { },
                    label = { Text(text = stringResource(id = R.string.read_date)) },
                    readOnly = true
                )
            }

            AuthorSelection(
                selected = uiState.selectedAuthor,
                onSelect = onSelectAuthor
            )

            GenreSelection(
                selected = uiState.selectedGenre,
                onSelect = onSelectGenre
            )

            BookFormDivider()

            BookRating(
                value = newBook.rating,
                onUpdate = { onUpdateBook(newBook.copy(rating = it)) }
            )

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                value = newBook.thought,
                onValueChange = { onUpdateBook(newBook.copy(thought = it)) },
                label = { Text(text = stringResource(id = R.string.thought)) },
                keyboardOptions = KeyboardOptions(KeyboardCapitalization.Sentences)
            )

            BookFormDivider()

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = newBook.memo ?: "",
                onValueChange = { onUpdateBook(newBook.copy(memo = it)) },
                label = { Text(text = stringResource(id = R.string.memo)) },
                keyboardOptions = KeyboardOptions(KeyboardCapitalization.Sentences)
            )
        }
    }
}

@Preview
@Composable
private fun NewBookScreenPreview() {
    KannaTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            NewBookScreen(
                isCompact = false,
                uiState = previewUiState,
                popBackStack = {},
                onUpdateBook = {},
                onSelectStatus = {},
                onSelectAuthor = {},
                onSelectGenre = {},
                onShowDatePicker = {},
                onCreateBook = {}
            )
        }
    }
}

@Composable
private fun StatusSelection(
    currentStatus: Book.Status,
    readStatuses: List<BookReadStatus>,
    onSelect: (BookReadStatus) -> Unit
) {
    Column(modifier = Modifier.selectableGroup()) {
        readStatuses.forEach { readStatus ->
            val selected = readStatus.status == currentStatus
            Row(
                modifier = Modifier
                    .selectable(
                        selected = selected,
                        onClick = { onSelect(readStatus) },
                        role = Role.RadioButton
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selected,
                    onClick = { onSelect(readStatus) }
                )

                Text(
                    text = readStatus.status.title(),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun Book.Status.title(): String =
    when (this) {
        Book.Status.HAVE_READ -> R.string.status_have_read
        Book.Status.READING_NOW -> R.string.status_reading_now
        Book.Status.READ_NEXT -> R.string.status_read_next
        Book.Status.WANT_TO_READ -> R.string.status_want_to_read
    }.let { id ->
        stringResource(id = id)
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
        SectionHeader(title = stringResource(id = R.string.rating))
        RatingStars(stars = value, onUpdate = onUpdate)
    }
}

@Preview @Composable
private fun BookRatingPreview() {
    PreviewColumnWrapper {
        BookRating(value = 3, onUpdate = {})
    }
}

@Composable
internal fun RatingStars(
    stars: Int,
    onUpdate: (rating: Int) -> Unit,
    spacedBy: Dp = 8.dp,
    size: Dp = 48.dp
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacedBy)
    ) {
        repeat(5) { index ->
            val coloured = stars > index
            val tint by animateColorAsState(
                targetValue = if (coloured) Color(0xFFFFDF00) else MaterialTheme.colorScheme.surfaceVariant
            )
            Icon(
                modifier = Modifier
                    .size(size)
                    .clickable { onUpdate(index + 1) },
                imageVector = if (coloured) Icons.Filled.Grade else Icons.Filled.StarOutline,
                contentDescription = null,
                tint = tint
            )
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
            genreId = "genre",
            statusId = 1
        ),
        statuses = Book.Status.values().mapIndexed { i, status -> BookReadStatus((i + 1).toLong(), status) },
        selectedStatus = Book.Status.HAVE_READ,
        selectedAuthor = Author(id = "", name = "author", memo = "", isFavourite = false),
        selectedGenre = "genre",
        showDatePicker = false
    )

@Composable
private fun NewBookScreenPreviewBase(isCompactScreen: Boolean) {
    KannaTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            NewBookDialog(
                isWidthCompact = isCompactScreen,
                onDismiss = {}
            ) {
                NewBookScreen(
                    isCompact = isCompactScreen,
                    uiState = previewUiState,
                    onUpdateBook = {},
                    onSelectStatus = {},
                    onSelectAuthor = {},
                    onSelectGenre = {},
                    popBackStack = {},
                    onShowDatePicker = {},
                    onCreateBook = {}
                )
            }
        }
    }
}
