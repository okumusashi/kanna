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
import com.hisui.kanna.core.model.Author
import com.hisui.kanna.core.model.NewBook
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.datetime.Clock
import javax.inject.Inject

data class NewBookUiState(
    val loading: Boolean,
    val error: String?,
    val newBook: NewBook,
    val authors: List<Author>,
    val genres: List<String>,
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
    val authors: List<Author> = emptyList(),
    val genres: List<String> = emptyList(),
    val showDatePicker: Boolean = false
) {
    fun toState(): NewBookUiState =
        NewBookUiState(
            loading = loading,
            error = error,
            newBook = newBook,
            authors = authors,
            genres = genres,
            showDatePicker = showDatePicker
        )
}

@HiltViewModel
class NewBookViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(NewBookViewModelState())
    val uiState: StateFlow<NewBookUiState>
        get() = _state
            .map { it.toState() }
            .stateIn(
                viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = NewBookViewModelState().toState()
            )

    fun updateBook(book: NewBook) {
        _state.update {
            it.copy(
                newBook = book,
                showDatePicker = false
            )
        }
    }

    fun showDatePicker() {
        _state.update { it.copy(showDatePicker = true) }
    }

    fun hideDatePicker() {
        _state.update { it.copy(showDatePicker = false) }
    }
}
