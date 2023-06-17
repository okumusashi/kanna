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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hisui.kanna.core.domain.usecase.GetFilteredBooksStreamUseCase
import com.hisui.kanna.core.model.BookForQuote
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.BUFFERED
import kotlinx.coroutines.delay
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

data class BookSelectionUiState(
    val title: String,
    val expanded: Boolean,
    val bookCandidates: List<BookForQuote>,
    val bookSelected: Boolean
)

private data class BookSelectionViewModelState(
    val title: String = "",
    val expanded: Boolean = false,
    val bookCandidates: List<BookForQuote> = emptyList(),
    val bookSelected: Boolean = false
) {
    fun toUiState(): BookSelectionUiState = BookSelectionUiState(
        title = title,
        expanded = expanded && !bookSelected,
        bookCandidates = bookCandidates,
        bookSelected = bookSelected
    )
}

@HiltViewModel
internal class BookSelectionViewModel @Inject constructor(
    private val getFilteredBooksStreamUseCase: GetFilteredBooksStreamUseCase
) : ViewModel() {

    private val viewModelState = MutableStateFlow(BookSelectionViewModelState())
    val uiState: StateFlow<BookSelectionUiState> = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            BookSelectionViewModelState().toUiState()
        )

    private val _focusEvent = Channel<Unit>(BUFFERED)
    val focusEvent: Flow<Unit> = _focusEvent.receiveAsFlow()

    fun filterBooks(q: String) {
        if (q.isBlank()) {
            viewModelState.update { it.copy(bookCandidates = emptyList()) }
            return
        }

        viewModelState.update { it.copy(title = q) }

        viewModelScope.launch {
            getFilteredBooksStreamUseCase(q = q).collect { books ->
                viewModelState.update { it.copy(bookCandidates = books) }
            }
        }
    }

    fun selectBook(title: String, selected: Boolean = true) {
        viewModelState.update {
            it.copy(title = title, bookSelected = selected)
        }
    }

    fun resetBook() {
        viewModelScope.launch {
            viewModelState.update {
                it.copy(
                    title = "",
                    bookSelected = false,
                    bookCandidates = emptyList()
                )
            }

            /**
             * FIXME:
             *  [androidx.compose.ui.focus.FocusRequester.requestFocus]  won't work without [delay].
             *  It might be a bug. Remove this [delay] when it's fixed.
             */
            delay(400)
            _focusEvent.send(Unit)
        }
    }

    fun changeDropdownExpanded() {
        viewModelState.update { it.copy(expanded = !it.expanded) }
    }

    fun shrinkDropdown() {
        viewModelState.update { it.copy(expanded = false) }
    }
}
