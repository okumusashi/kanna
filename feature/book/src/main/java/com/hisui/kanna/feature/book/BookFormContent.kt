/*
 * Copyright 2023 Lynn Sakashita
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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.hisui.kanna.core.model.Author
import com.hisui.kanna.core.model.BookForm
import com.hisui.kanna.core.model.BookReadStatus
import com.hisui.kanna.core.model.BookStatus
import com.hisui.kanna.core.ui.R
import com.hisui.kanna.core.ui.component.KannaTopBar
import com.hisui.kanna.core.ui.preview.PreviewColumnWrapper
import com.hisui.kanna.core.ui.util.dateTimeFormatter
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant

@Composable
internal fun BookFormDialog(
    isCompact: Boolean,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    Dialog(
        properties = DialogProperties(
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = !isCompact
        ),
        onDismissRequest = onDismiss
    ) {
        content()
    }
}

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
                Text(text = stringResource(id = R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(id = R.string.cancel))
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
internal fun BookFormContent(
    isCompact: Boolean,
    title: String,
    submitButtonTitle: String,
    book: BookForm,
    selectedAuthor: Author?,
    selectedGenre: String?,
    statuses: List<BookReadStatus>,
    selectedStatus: BookStatus,
    popBackStack: () -> Unit,
    onUpdateBook: (BookForm) -> Unit,
    onSelectStatus: (BookReadStatus) -> Unit,
    onSelectAuthor: (Author) -> Unit,
    onSelectGenre: (String) -> Unit,
    onSubmit: (BookForm) -> Unit
) {
    var datePickerShown by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(if (isCompact) 1f else 0.65f),
        topBar = {
            KannaTopBar(
                title = title,
                submitButtonTitle = submitButtonTitle,
                onClickNavigationIcon = popBackStack,
                onSubmit = { onSubmit(book) }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = book.title,
                    onValueChange = { onUpdateBook(book.copy(title = it)) },
                    label = { Text(text = stringResource(id = com.hisui.kanna.feature.book.R.string.title)) },
                    keyboardOptions = KeyboardOptions(KeyboardCapitalization.Sentences)
                )
            }

            item {
                StatusSelection(
                    currentStatus = selectedStatus,
                    readStatuses = statuses,
                    onSelect = onSelectStatus
                )
            }

            item {
                AnimatedVisibility(visible = selectedStatus == BookStatus.HAVE_READ) {
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = {
                            Icon(
                                modifier = Modifier.clickable { datePickerShown = true },
                                imageVector = Icons.Filled.EditCalendar,
                                contentDescription = "Calendar"
                            )
                        },
                        value = dateTimeFormatter().format(book.readDate.toJavaInstant()),
                        onValueChange = { },
                        label = { Text(text = stringResource(id = com.hisui.kanna.feature.book.R.string.read_date)) },
                        readOnly = true
                    )
                }
            }

            item {
                AuthorSelection(
                    selected = selectedAuthor,
                    onSelect = onSelectAuthor
                )
            }

            item {
                GenreSelection(
                    selected = selectedGenre,
                    onSelect = onSelectGenre
                )
            }

            item { BookFormDivider() }

            item {
                BookRating(
                    value = book.rating,
                    onUpdate = { onUpdateBook(book.copy(rating = it)) }
                )
            }

            item {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    value = book.thought,
                    onValueChange = { onUpdateBook(book.copy(thought = it)) },
                    label = { Text(text = stringResource(id = R.string.thought)) },
                    keyboardOptions = KeyboardOptions(KeyboardCapitalization.Sentences)
                )
            }

            item { BookFormDivider() }

            item {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = book.memo ?: "",
                    onValueChange = { onUpdateBook(book.copy(memo = it)) },
                    label = { Text(text = stringResource(id = com.hisui.kanna.feature.book.R.string.memo)) },
                    keyboardOptions = KeyboardOptions(KeyboardCapitalization.Sentences)
                )
            }

            item { Spacer(modifier = Modifier.height(64.dp)) }
        }
    }

    if (datePickerShown) {
        ReadDatePicker(
            onDismiss = { datePickerShown = false },
            onUpdate = {
                onUpdateBook(book.copy(readDate = it))
                datePickerShown = false
            }
        )
    }
}

@Composable
private fun StatusSelection(
    currentStatus: BookStatus,
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
private fun BookStatus.title(): String =
    when (this) {
        BookStatus.HAVE_READ -> com.hisui.kanna.feature.book.R.string.status_have_read
        BookStatus.READING_NOW -> com.hisui.kanna.feature.book.R.string.status_reading_now
        BookStatus.READ_NEXT -> com.hisui.kanna.feature.book.R.string.status_read_next
        BookStatus.WANT_TO_READ -> com.hisui.kanna.feature.book.R.string.status_want_to_read
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
        SectionHeader(title = stringResource(id = com.hisui.kanna.feature.book.R.string.rating))
        RatingStars(stars = value, onUpdate = onUpdate)
    }
}

@Preview
@Composable
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
