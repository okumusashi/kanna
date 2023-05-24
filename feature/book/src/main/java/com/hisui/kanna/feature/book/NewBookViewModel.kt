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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hisui.kanna.core.data.repository.BookRepository
import com.hisui.kanna.core.model.Author
import com.hisui.kanna.core.model.NewBook
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
import kotlinx.datetime.Clock
import javax.inject.Inject

internal data class NewBookUiState(
    val loading: Boolean,
    val error: String?,
    val newBook: NewBook,
    val selectedAuthor: Author?,
    val selectedGenre: String?,
    val showDatePicker: Boolean
)

private data class NewBookViewModelState(
    val loading: Boolean = false,
    val error: String? = null,
    val newBook: NewBook = NewBook(
        title = "",
        readDate = Clock.System.now(),
        memo = "",
        thought = "",
        rating = 0,
        authorId = "",
        genreId = "",
    ),
    val selectedAuthor: Author? = null,
    val showDatePicker: Boolean = false
) {
    fun toState(): NewBookUiState =
        NewBookUiState(
            loading = loading,
            error = error,
            newBook = newBook,
            selectedAuthor = selectedAuthor,
            selectedGenre = newBook.genreId,
            showDatePicker = showDatePicker
        )
}

sealed interface NewBookEvent {
    object Created : NewBookEvent
}

@HiltViewModel
internal class NewBookViewModel @Inject constructor(
    private val repository: BookRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(NewBookViewModelState())
    val uiState: StateFlow<NewBookUiState>
        get() = _state
            .map { it.toState() }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = NewBookViewModelState().toState()
            )

    private val _event = Channel<NewBookEvent>(BUFFERED)
    val event: Flow<NewBookEvent> = _event.receiveAsFlow()

    fun updateBook(book: NewBook) {
        _state.update {
            it.copy(
                newBook = book,
                showDatePicker = false
            )
        }
    }

    fun createBook(book: NewBook) {
        viewModelScope.launch {
            val result = repository.save(book = book)
            if (result.isSuccess) {
                _event.send(NewBookEvent.Created)
            } else {
                _state.update { it.copy(error = result.exceptionOrNull()?.message) }
            }
        }
    }

    fun showDatePicker() {
        _state.update { it.copy(showDatePicker = true) }
    }

    fun dismissDatePicker() {
        _state.update { it.copy(showDatePicker = false) }
    }

    fun selectAuthor(author: Author) {
        _state.update {
            it.copy(
                selectedAuthor = author,
                newBook = it.newBook.copy(authorId = author.id),
            )
        }
    }

    fun selectGenre(genre: String) {
        _state.update {
            it.copy(newBook = it.newBook.copy(genreId = genre))
        }
    }
}
