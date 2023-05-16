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
import com.hisui.kanna.core.domain.model.AuthorError
import com.hisui.kanna.core.domain.usecase.CreateAuthorUseCase
import com.hisui.kanna.core.domain.usecase.GetAllAuthorsStreamUseCase
import com.hisui.kanna.core.model.Author
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

sealed interface AuthorSelectionUiState {
    object Loading : AuthorSelectionUiState

    data class NoAuthor(
        val showCreateDialog: Boolean,
        val error: AuthorError?,
    ) : AuthorSelectionUiState

    data class ShowList(
        val authors: List<Author>,
        val showCreateDialog: Boolean,
        val error: AuthorError?,
    ) : AuthorSelectionUiState
}

private data class AuthorSelectionViewModelState(
    val loading: Boolean = true,
    val authors: List<Author> = emptyList(),
    val showCreateDialog: Boolean = false,
    val error: AuthorError? = null
) {
    fun toUiState(): AuthorSelectionUiState =
        when {
            loading -> AuthorSelectionUiState.Loading

            authors.isEmpty() ->
                AuthorSelectionUiState.NoAuthor(showCreateDialog = showCreateDialog, error = error)

            else ->
                AuthorSelectionUiState.ShowList(
                    authors = authors,
                    showCreateDialog = showCreateDialog,
                    error = error,
                )
        }
}

sealed interface AuthorSelectionEvent {
    data class Select(val author: Author) : AuthorSelectionEvent
}

@HiltViewModel
class AuthorSelectionViewModel @Inject constructor(
    private val getAllAuthorsStreamUseCase: GetAllAuthorsStreamUseCase,
    private val createAuthorUseCase: CreateAuthorUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(AuthorSelectionViewModelState())
    val state: StateFlow<AuthorSelectionUiState> = _state
        .map { it.toUiState() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = AuthorSelectionUiState.Loading
        )

    private val _event = Channel<AuthorSelectionEvent>(BUFFERED)
    val event: Flow<AuthorSelectionEvent> = _event.receiveAsFlow()

    init {
        viewModelScope.launch {
            getAllAuthorsStreamUseCase().collect { authors ->
                _state.update { it.copy(authors = authors, loading = false) }
            }
        }
    }

    fun showCreateDialog() {
        _state.update { it.copy(showCreateDialog = true) }
    }

    fun dismissCreateDialog() {
        _state.update { it.copy(showCreateDialog = false) }
    }

    fun createAuthor(name: String, memo: String?) {
        viewModelScope.launch {
            _state.update { it.copy(loading = true) }
            val result = createAuthorUseCase(name = name, memo = memo)
            when {
                result.isFailure -> _state.update { it.copy(error = AuthorError.Create()) }
                result.isSuccess -> {
                    dismissCreateDialog()
                    result.getOrNull()
                        ?.let(AuthorSelectionEvent::Select)
                        ?.let { _event.send(it) }
                }
            }
        }
    }
}
