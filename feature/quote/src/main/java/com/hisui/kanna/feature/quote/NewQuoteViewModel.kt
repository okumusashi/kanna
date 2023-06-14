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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hisui.kanna.core.data.repository.QuoteRepository
import com.hisui.kanna.core.domain.usecase.GetFilteredBooksStreamUseCase
import com.hisui.kanna.core.model.BookForQuote
import com.hisui.kanna.core.model.QuoteForm
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.BUFFERED
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface NewQuoteUiState {

    object Loading : NewQuoteUiState

    data class AddQuote(
        val error: String?,
        val quoteForm: QuoteForm,
        val selectedBook: BookForQuote?,
        val bookCandidates: List<BookForQuote>
    ) : NewQuoteUiState
}

private data class NewQuoteViewModelState(
    val loading: Boolean = true,
    val error: String? = null,
    val quoteForm: QuoteForm = QuoteForm(
        quote = "",
        bookId = 0,
        page = null,
        thought = ""
    ),
    val selectedBook: BookForQuote? = null,
    // Note: Move below to another ViewModel if necessary
    val bookQuery: String = "",
    val bookCandidates: List<BookForQuote> = emptyList()
) {
    fun toState(): NewQuoteUiState =
        when {
            loading -> NewQuoteUiState.Loading
            else -> {
                NewQuoteUiState.AddQuote(
                    error = error,
                    quoteForm = quoteForm,
                    selectedBook = selectedBook,
                    bookCandidates = bookCandidates
                )
            }
        }
}

sealed interface NewQuoteEvent {
    object Created : NewQuoteEvent
}

@HiltViewModel
internal class NewQuoteViewModel @Inject constructor(
    private val repository: QuoteRepository,
    private val getFilteredBooksStreamUseCase: GetFilteredBooksStreamUseCase
) : ViewModel() {

    private val viewModelState = MutableStateFlow(NewQuoteViewModelState())
    val uiState: StateFlow<NewQuoteUiState> = viewModelState
        .map { it.toState() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = NewQuoteViewModelState().toState()
        )

    private val _event = Channel<NewQuoteEvent>(BUFFERED)
    val event: Flow<NewQuoteEvent> = _event.receiveAsFlow()

    init {
        // TODO
        viewModelState.update { it.copy(loading = false) }
    }

    // Note: Move to another ViewModel if necessary
    fun filterBooks(q: String) {
        if (q.isBlank()) {
            viewModelState.update { it.copy(bookCandidates = emptyList()) }
        }

        viewModelScope.launch {
            getFilteredBooksStreamUseCase(q = q).collect { books ->
                viewModelState.update { it.copy(bookCandidates = books) }
            }
        }
    }

    fun updateQuote(quote: QuoteForm) {
        viewModelState.update { it.copy(quoteForm = quote) }
    }

    fun selectBook(book: BookForQuote) {
        viewModelState.update { it.copy(selectedBook = book) }
    }

    fun create(quoteForm: QuoteForm) {
        viewModelScope.launch {
            val result = repository.save(quote = quoteForm)
            if (result.isSuccess) {
                _event.send(NewQuoteEvent.Created)
            }
            if (result.isFailure) {
                // TODO
            }
        }
    }
}
