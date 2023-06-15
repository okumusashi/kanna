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

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hisui.kanna.core.KannaError
import com.hisui.kanna.core.Result
import com.hisui.kanna.core.data.repository.QuoteRepository
import com.hisui.kanna.core.domain.usecase.GetBookForQuoteUseCase
import com.hisui.kanna.core.domain.usecase.GetQuoteStreamUseCase
import com.hisui.kanna.core.model.BookForQuote
import com.hisui.kanna.core.model.Quote
import com.hisui.kanna.core.model.QuoteForm
import com.hisui.kanna.feature.quote.navigation.QuoteArgs
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

data class EditQuoteUiState(
    val loading: Boolean,
    val error: KannaError?,
    val quoteForm: QuoteForm,
    val selectedBook: BookForQuote?,
    val submittable: Boolean
)

private data class EditQuoteViewModelState(
    val loading: Boolean = true,
    val error: KannaError? = null,
    val quoteForm: QuoteForm = QuoteForm(
        quote = "",
        page = null,
        bookId = 0,
        thought = ""
    ),
    val selectedBook: BookForQuote? = null
) {
    fun toState(): EditQuoteUiState =
        EditQuoteUiState(
            loading = loading,
            error = error,
            quoteForm = quoteForm,
            selectedBook = selectedBook,
            submittable = !loading && error == null
        )
}

sealed interface EditQuoteEvent {
    data class Complete(val id: Long) : EditQuoteEvent
}

@HiltViewModel
class EditQuoteViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getQuoteStreamUseCase: GetQuoteStreamUseCase,
    getBookForQuoteUseCase: GetBookForQuoteUseCase,
    private val repository: QuoteRepository
) : ViewModel() {

    private val args = QuoteArgs(savedStateHandle)

    private val viewModelState = MutableStateFlow(EditQuoteViewModelState())
    val uiState: StateFlow<EditQuoteUiState> = viewModelState
        .map { it.toState() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = EditQuoteViewModelState().toState()
        )

    private val _event = Channel<EditQuoteEvent>(BUFFERED)
    val event: Flow<EditQuoteEvent> = _event.receiveAsFlow()

    init {
        viewModelScope.launch {
            getQuoteStreamUseCase(id = args.quoteId).collect { result ->
                when (result) {
                    is Result.Loading -> { /* do nothing */ }
                    is Result.Error -> viewModelState.update { it.copy(error = result.error) }
                    is Result.Success ->
                        viewModelState.update { state ->
                            state.copy(
                                loading = false,
                                error = null,
                                quoteForm = result.data.asForm(),
                                selectedBook = getBookForQuoteUseCase(id = result.data.bookId)
                            )
                        }
                }
            }
        }
    }

    fun updateQuote(quote: QuoteForm) {
        viewModelState.update { it.copy(quoteForm = quote) }
    }

    fun selectBook(book: BookForQuote) {
        viewModelState.update { it.copy(selectedBook = book) }
    }

    fun update(quoteForm: QuoteForm) {
        viewModelScope.launch {
            val result = repository.update(id = args.quoteId, quote = quoteForm)
            if (result.isSuccess) {
                _event.send(EditQuoteEvent.Complete(id = args.quoteId))
            }
            if (result.isFailure) {
                // TODO
            }
        }
    }
}

private fun Quote.asForm(): QuoteForm =
    QuoteForm(
        quote = quote,
        bookId = bookId,
        page = page,
        thought = thought
    )
