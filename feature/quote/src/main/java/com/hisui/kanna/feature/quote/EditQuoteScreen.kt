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
internal fun EditQuoteRoute(
    viewModel: EditQuoteViewModel = hiltViewModel(),
    isCompact: Boolean,
    onOpenQuote: (id: Long) -> Unit,
    onExit: () -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is EditQuoteEvent.Complete -> onOpenQuote(event.id)
            }
        }
    }

    val uiState by viewModel.uiState.collectAsState()

    EditQuoteScreen(
        isCompact = isCompact,
        uiState = uiState,
        onUpdateQuote = viewModel::updateQuote,
        onSelectBook = viewModel::selectBook,
        onUpdate = viewModel::update,
        onExit = onExit
    )
}

@Composable
internal fun EditQuoteScreen(
    isCompact: Boolean,
    uiState: EditQuoteUiState,
    onUpdateQuote: (QuoteForm) -> Unit,
    onSelectBook: (BookForQuote) -> Unit,
    onUpdate: (quote: QuoteForm) -> Unit,
    onExit: () -> Unit
) {
    QuoteFormBase(
        isCompact = isCompact,
        title = stringResource(id = R.string.edit_quote),
        submitButtonTitle = stringResource(id = com.hisui.kanna.core.ui.R.string.update),
        onSubmit = { onUpdate(uiState.quoteForm) },
        submittable = uiState.submittable,
        onExit = onExit
    ) { paddingValues ->
        QuoteFormContent(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .padding(16.dp),
            quoteForm = uiState.quoteForm,
            selectedBookTitle = uiState.selectedBook?.title,
            onUpdateQuote = onUpdateQuote,
            onSelectBook = onSelectBook
        )
    }
}

@Composable
private fun EditQuoteScreenPreviewBase(isCompact: Boolean) {
    KannaTheme {
        EditQuoteScreen(
            isCompact = isCompact,
            uiState = EditQuoteUiState(
                loading = false,
                error = null,
                quoteForm = QuoteForm(
                    quote = "quote",
                    bookId = 0L,
                    page = 1,
                    thought = "thought"
                ),
                selectedBook = null,
                submittable = true
            ),
            onUpdateQuote = {},
            onSelectBook = {},
            onUpdate = {},
            onExit = {}
        )
    }
}

@Preview(device = Devices.PIXEL_4)
@Composable
private fun EditQuoteScreenPhonePreview() { EditQuoteScreenPreviewBase(isCompact = true) }

@Preview(device = Devices.TABLET)
@Composable
private fun EditQuoteScreenTablePreview() { EditQuoteScreenPreviewBase(isCompact = false) }

@Preview(device = Devices.DESKTOP)
@Composable
private fun EditQuoteScreenDesktopPreview() { EditQuoteScreenPreviewBase(isCompact = false) }
