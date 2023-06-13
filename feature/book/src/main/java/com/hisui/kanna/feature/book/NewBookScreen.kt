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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.hisui.kanna.core.designsystem.theme.KannaTheme
import com.hisui.kanna.core.model.Author
import com.hisui.kanna.core.model.BookForm
import com.hisui.kanna.core.model.BookReadStatus
import com.hisui.kanna.core.model.BookStatus
import com.hisui.kanna.feature.book.component.BookFormContent
import com.hisui.kanna.feature.book.component.BookFormDialog
import kotlinx.datetime.Clock

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

    BookFormDialog(
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
            onCreateBook = viewModel::createBook
        )
    }
}

@Composable
internal fun NewBookScreen(
    isCompact: Boolean,
    uiState: NewBookUiState,
    popBackStack: () -> Unit,
    onUpdateBook: (BookForm) -> Unit,
    onSelectStatus: (BookReadStatus) -> Unit,
    onSelectAuthor: (Author) -> Unit,
    onSelectGenre: (String) -> Unit,
    onCreateBook: (BookForm) -> Unit
) {
    BookFormContent(
        isCompact = isCompact,
        title = stringResource(id = R.string.new_book),
        submitButtonTitle = stringResource(id = com.hisui.kanna.core.ui.R.string.create),
        book = uiState.newBook,
        statuses = uiState.statuses,
        selectedStatus = uiState.selectedStatus,
        selectedAuthor = uiState.selectedAuthor,
        selectedGenre = uiState.selectedGenre,
        popBackStack = popBackStack,
        onUpdateBook = onUpdateBook,
        onSelectStatus = onSelectStatus,
        onSelectAuthor = onSelectAuthor,
        onSelectGenre = onSelectGenre,
        onSubmit = onCreateBook
    )
}

private val previewUiState: NewBookUiState =
    NewBookUiState(
        loading = false,
        error = null,
        newBook = BookForm(
            title = "title",
            readDate = Clock.System.now(),
            thought = "",
            memo = "",
            rating = 3,
            authorId = "author",
            genreId = "genre",
            statusId = 1
        ),
        statuses = BookStatus.values().mapIndexed { i, status -> BookReadStatus((i + 1).toLong(), status) },
        selectedStatus = BookStatus.HAVE_READ,
        selectedAuthor = Author(id = "", name = "author", memo = "", isFavourite = false),
        selectedGenre = "genre"
    )

@Composable
private fun NewBookScreenPreviewBase(isCompactScreen: Boolean) {
    KannaTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            BookFormDialog(
                isWidthCompact = isCompactScreen,
                onDismiss = {}
            ) {
                BookFormContent(
                    isCompact = isCompactScreen,
                    title = "New book",
                    submitButtonTitle = "Create",
                    book = previewUiState.newBook,
                    statuses = previewUiState.statuses,
                    selectedStatus = previewUiState.selectedStatus,
                    selectedAuthor = previewUiState.selectedAuthor,
                    selectedGenre = previewUiState.selectedGenre,
                    onUpdateBook = {},
                    onSelectStatus = {},
                    onSelectAuthor = {},
                    onSelectGenre = {},
                    popBackStack = {},
                    onSubmit = {}
                )
            }
        }
    }
}

@Preview @Composable
private fun NewBookScreenPhonePreview() { NewBookScreenPreviewBase(isCompactScreen = true) }

@Preview(device = Devices.TABLET)
@Composable
private fun NewBookScreenTablePreview() { NewBookScreenPreviewBase(isCompactScreen = false) }

@Preview(device = Devices.DESKTOP)
@Composable
private fun NewBookScreenDesktopPreview() { NewBookScreenPreviewBase(isCompactScreen = false) }
