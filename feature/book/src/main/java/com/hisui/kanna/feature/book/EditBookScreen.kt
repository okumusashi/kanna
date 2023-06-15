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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.hisui.kanna.core.model.Author
import com.hisui.kanna.core.model.BookForm
import com.hisui.kanna.core.model.BookReadStatus

@Composable
internal fun EditBookRoute(
    viewModel: EditBookViewModel = hiltViewModel(),
    isWidthCompact: Boolean,
    isHeightCompact: Boolean,
    popBackStack: () -> Unit,
    openBook: (id: Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is EditBookEvent.Completed -> openBook(event.id)
            }
        }
    }

    BookFormDialog(
        isWidthCompact = isWidthCompact,
        onDismiss = popBackStack
    ) {
        EditBookScreen(
            isCompact = isWidthCompact || isHeightCompact,
            uiState = uiState,
            popBackStack = popBackStack,
            onUpdateBook = viewModel::updateBook,
            onSelectStatus = viewModel::selectStatus,
            onSelectAuthor = viewModel::selectAuthor,
            onSelectGenre = viewModel::selectGenre,
            onUpdate = viewModel::submitBookUpdate
        )
    }
}

@Composable
internal fun EditBookScreen(
    isCompact: Boolean,
    uiState: EditBookUiState,
    popBackStack: () -> Unit,
    onUpdateBook: (BookForm) -> Unit,
    onSelectStatus: (BookReadStatus) -> Unit,
    onSelectAuthor: (Author) -> Unit,
    onSelectGenre: (String) -> Unit,
    onUpdate: (BookForm) -> Unit
) {
    when (uiState) {
        EditBookUiState.Loading -> {}
        is EditBookUiState.Error -> {}
        is EditBookUiState.Success ->
            BookFormContent(
                isCompact = isCompact,
                title = stringResource(id = R.string.edit_book),
                submitButtonTitle = stringResource(id = com.hisui.kanna.core.ui.R.string.update),
                book = uiState.book,
                selectedAuthor = uiState.selectedAuthor,
                selectedGenre = uiState.selectedGenre,
                statuses = uiState.statuses,
                selectedStatus = uiState.selectedStatus,
                popBackStack = popBackStack,
                onUpdateBook = onUpdateBook,
                onSelectStatus = onSelectStatus,
                onSelectAuthor = onSelectAuthor,
                onSelectGenre = onSelectGenre,
                onSubmit = onUpdate
            )
    }
}
