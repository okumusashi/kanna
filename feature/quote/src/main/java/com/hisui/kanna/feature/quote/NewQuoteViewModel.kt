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
import com.hisui.kanna.core.model.DEFAULT_BOOK_ID
import com.hisui.kanna.core.model.QuoteForm
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.BUFFERED
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NewQuoteUiState(
    val loading: Boolean = true,
    val error: String? = null,
    val quoteForm: QuoteForm = QuoteForm(
        quote = "",
        bookId = DEFAULT_BOOK_ID,
        page = null,
        thought = ""
    ),
    val selectedBook: BookForQuote? = null,
    val submittable: Boolean = false
)

sealed interface NewQuoteEvent {
    object Created : NewQuoteEvent
}

@HiltViewModel
internal class NewQuoteViewModel @Inject constructor(
    private val repository: QuoteRepository,
    private val getFilteredBooksStreamUseCase: GetFilteredBooksStreamUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewQuoteUiState())
    val uiState: StateFlow<NewQuoteUiState> = _uiState

    private val _event = Channel<NewQuoteEvent>(BUFFERED)
    val event: Flow<NewQuoteEvent> = _event.receiveAsFlow()

    init {
        // TODO
        _uiState.update { it.copy(loading = false) }
    }

    fun updateQuote(quote: QuoteForm) {
        _uiState.update { it.copy(quoteForm = quote) }
    }

    fun selectBook(book: BookForQuote) {
        _uiState.update { it.copy(selectedBook = book) }
    }

    fun updateSubmittable(submittable: Boolean) {
        _uiState.update { it.copy(submittable = submittable) }
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
