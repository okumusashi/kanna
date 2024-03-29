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
import com.hisui.kanna.core.domain.usecase.GetAllStatusUseCase
import com.hisui.kanna.core.model.Author
import com.hisui.kanna.core.model.BookForm
import com.hisui.kanna.core.model.BookReadStatus
import com.hisui.kanna.core.model.BookStatus
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
    val newBook: BookForm,
    val statuses: List<BookReadStatus>,
    val selectedStatus: BookStatus,
    val selectedAuthor: Author?,
    val selectedGenre: String?
)

private data class NewBookViewModelState(
    val loading: Boolean = false,
    val error: String? = null,
    val newBook: BookForm = BookForm(
        title = "",
        readDate = Clock.System.now(),
        memo = "",
        thought = "",
        rating = 0,
        authorId = "",
        genreId = "",
        statusId = 1
    ),
    val statuses: List<BookReadStatus> = emptyList(),
    val selectedStatus: BookStatus = BookStatus.HAVE_READ,
    val selectedAuthor: Author? = null
) {
    fun toState(): NewBookUiState =
        NewBookUiState(
            loading = loading,
            error = error,
            newBook = newBook,
            statuses = statuses,
            selectedStatus = selectedStatus,
            selectedAuthor = selectedAuthor,
            selectedGenre = newBook.genreId
        )
}

sealed interface NewBookEvent {
    object Created : NewBookEvent
}

@HiltViewModel
internal class NewBookViewModel @Inject constructor(
    private val bookRepository: BookRepository,
    private val getAllStatusUseCase: GetAllStatusUseCase
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

    init {
        viewModelScope.launch {
            _state.update { it.copy(statuses = getAllStatusUseCase()) }
        }
    }

    fun updateBook(book: BookForm) {
        _state.update {
            it.copy(newBook = book)
        }
    }

    fun createBook(book: BookForm) {
        viewModelScope.launch {
            val result = bookRepository.save(book = book)
            if (result.isSuccess) {
                _event.send(NewBookEvent.Created)
            } else {
                _state.update { it.copy(error = result.exceptionOrNull()?.message) }
            }
        }
    }

    fun selectStatus(readStatus: BookReadStatus) {
        _state.update {
            it.copy(
                selectedStatus = readStatus.status,
                newBook = it.newBook.copy(statusId = readStatus.id)
            )
        }
    }

    fun selectAuthor(author: Author) {
        _state.update {
            it.copy(
                selectedAuthor = author,
                newBook = it.newBook.copy(authorId = author.id)
            )
        }
    }

    fun selectGenre(genre: String) {
        _state.update {
            it.copy(newBook = it.newBook.copy(genreId = genre))
        }
    }
}
