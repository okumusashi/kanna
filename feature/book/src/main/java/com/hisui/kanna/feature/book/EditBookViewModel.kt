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

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hisui.kanna.core.KannaError
import com.hisui.kanna.core.Result
import com.hisui.kanna.core.data.repository.BookRepository
import com.hisui.kanna.core.domain.error.BookError
import com.hisui.kanna.core.domain.usecase.GetAllStatusUseCase
import com.hisui.kanna.core.domain.usecase.GetBookStreamUseCase
import com.hisui.kanna.core.model.Author
import com.hisui.kanna.core.model.Book
import com.hisui.kanna.core.model.BookForm
import com.hisui.kanna.core.model.BookReadStatus
import com.hisui.kanna.core.model.BookStatus
import com.hisui.kanna.feature.book.navigation.BookArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
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

sealed interface EditBookUiState {

    object Loading : EditBookUiState

    data class Error(val error: KannaError) : EditBookUiState

    data class Success(
        val book: BookForm,
        val statuses: List<BookReadStatus>,
        val selectedStatus: BookStatus,
        val selectedAuthor: Author?,
        val selectedGenre: String?
    ) : EditBookUiState
}

private data class EditBookViewModelState(
    val book: BookForm? = null,
    val error: KannaError? = null,
    val statuses: List<BookReadStatus> = emptyList(),
    val selectedStatus: BookStatus = BookStatus.HAVE_READ,
    val selectedGenre: String? = null,
    val selectedAuthor: Author? = null
) {
    fun toState(): EditBookUiState =
        when {
            book == null -> EditBookUiState.Loading
            error != null -> EditBookUiState.Error(error)
            else ->
                EditBookUiState.Success(
                    book = book,
                    statuses = statuses,
                    selectedStatus = selectedStatus,
                    selectedAuthor = selectedAuthor,
                    selectedGenre = selectedGenre ?: ""
                )
        }
}

sealed interface EditBookEvent {
    data class Completed(val id: Long) : EditBookEvent
}

@HiltViewModel
internal class EditBookViewModel @Inject constructor(
    private val bookRepository: BookRepository,
    private val getAllStatusUseCase: GetAllStatusUseCase,
    savedStateHandle: SavedStateHandle,
    getBookStreamUseCase: GetBookStreamUseCase
) : ViewModel() {

    private val args = BookArgs(savedStateHandle)

    private val _uiState = MutableStateFlow(EditBookViewModelState())
    val uiState: StateFlow<EditBookUiState>
        get() = _uiState
            .map { it.toState() }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = EditBookViewModelState().toState()
            )

    private val _event = Channel<EditBookEvent>(Channel.BUFFERED)
    val event: Flow<EditBookEvent> = _event.receiveAsFlow()

    init {
        viewModelScope.launch {
            launch {
                getBookStreamUseCase(args.bookId).collect { result ->
                    when (result) {
                        Result.Loading -> { /* do nothing */ }

                        is Result.Error ->
                            _uiState.update { it.copy(error = result.error) }

                        is Result.Success ->
                            _uiState.update {
                                val book = result.data
                                it.copy(
                                    book = book.asForm(),
                                    selectedAuthor = book.author,
                                    selectedGenre = book.genre,
                                    selectedStatus = book.status.status,
                                    error = null
                                )
                            }
                    }
                }
            }
            launch {
                _uiState.update { it.copy(statuses = getAllStatusUseCase()) }
            }
        }
    }

    fun updateBook(book: BookForm) {
        _uiState.update {
            it.copy(book = book)
        }
    }

    fun submitBookUpdate(bookForm: BookForm) {
        viewModelScope.launch {
            val result = bookRepository.update(id = args.bookId, book = bookForm)
            if (result.isSuccess) {
                _event.send(EditBookEvent.Completed(id = args.bookId))
            } else {
                // TODO: Log to firebase
                _uiState.update { it.copy(error = BookError.UpdateFailed) }
            }
        }
    }

    fun selectStatus(readStatus: BookReadStatus) {
        _uiState.update {
            it.copy(selectedStatus = readStatus.status)
        }
    }

    fun selectAuthor(author: Author) {
        _uiState.update {
            it.copy(selectedAuthor = author)
        }
    }

    fun selectGenre(genre: String) {
        _uiState.update {
            it.copy(selectedGenre = genre)
        }
    }
}

private fun Book.asForm(): BookForm =
    BookForm(
        title = title,
        readDate = readDate,
        memo = memo,
        thought = thought,
        rating = rating,
        authorId = author.id,
        genreId = genre,
        statusId = status.id
    )
