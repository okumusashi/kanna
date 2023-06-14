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

package com.hisui.kanna.feature.quote

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hisui.kanna.core.designsystem.theme.KannaTheme
import com.hisui.kanna.core.model.BookForQuote
import com.hisui.kanna.core.model.QuoteForm

@Composable
internal fun NewQuoteRoute(
    viewModel: NewQuoteViewModel = hiltViewModel(),
    isWidthCompact: Boolean,
    isHeightCompact: Boolean,
    popBackStack: () -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                NewQuoteEvent.Created -> popBackStack()
            }
        }
    }

    val uiState by viewModel.uiState.collectAsState()

    NewQuoteScreen(
        isCompact = isHeightCompact || isWidthCompact,
        uiState = uiState,
        onUpdateQuote = viewModel::updateQuote,
        onSelectBook = viewModel::selectBook,
        onUpdateBookFilter = viewModel::filterBooks,
        onCreate = viewModel::create,
        onExit = popBackStack
    )
}

@Composable
internal fun NewQuoteScreen(
    isCompact: Boolean,
    uiState: NewQuoteUiState,
    onUpdateQuote: (QuoteForm) -> Unit,
    onSelectBook: (BookForQuote) -> Unit,
    onUpdateBookFilter: (q: String) -> Unit,
    onCreate: (QuoteForm) -> Unit,
    onExit: () -> Unit
) {
    QuoteFormBase(
        isCompact = isCompact,
        title = stringResource(id = R.string.add_quote),
        submitButtonTitle = stringResource(id = com.hisui.kanna.core.ui.R.string.create),
        onSubmit = {
            if (uiState is NewQuoteUiState.AddQuote) {
                onCreate(uiState.quoteForm)
            }
        },
        submittable = uiState is NewQuoteUiState.AddQuote,
        onExit = onExit
    ) { paddingValues ->
        when (uiState) {
            NewQuoteUiState.Loading -> {}
            is NewQuoteUiState.AddQuote -> {
                QuoteFormContent(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(paddingValues)
                        .padding(16.dp),
                    quoteForm = uiState.quoteForm,
                    bookCandidates = uiState.bookCandidates,
                    onUpdateQuote = onUpdateQuote,
                    onUpdateBookFilter = onUpdateBookFilter,
                    onSelectBook = onSelectBook
                )
            }
        }
    }
}

@Composable
private fun NewQuoteScreenPreviewBase(isCompact: Boolean) {
    KannaTheme {
        NewQuoteScreen(
            isCompact = isCompact,
            uiState = NewQuoteUiState.AddQuote(
                error = null,
                quoteForm = QuoteForm(quote = "", thought = "", bookId = 1L, page = 1),
                selectedBook = null,
                bookCandidates = emptyList()
            ),
            onUpdateQuote = {},
            onSelectBook = {},
            onUpdateBookFilter = {},
            onCreate = {},
            onExit = {}
        )
    }
}

@Preview(device = Devices.PIXEL_4)
@Composable
private fun NewQuoteScreenPhonePreview() { NewQuoteScreenPreviewBase(isCompact = true) }

@Preview(device = Devices.TABLET)
@Composable
private fun NewQuoteScreenTabletPreview() { NewQuoteScreenPreviewBase(isCompact = false) }

@Preview(device = Devices.DESKTOP)
@Composable
private fun NewQuoteScreenDesktopPreview() { NewQuoteScreenPreviewBase(isCompact = false) }
