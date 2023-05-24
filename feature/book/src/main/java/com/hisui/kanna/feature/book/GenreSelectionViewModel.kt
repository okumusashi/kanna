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
import com.hisui.kanna.core.domain.model.GenreError
import com.hisui.kanna.core.domain.usecase.CreateGenreUseCase
import com.hisui.kanna.core.domain.usecase.GetAllGenreNameStreamUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.BUFFERED
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface GenreSelectionUiState {
    val showCreateDialog: Boolean
    object Loading : GenreSelectionUiState {
        override val showCreateDialog: Boolean = false
    }

    data class NoGenre(
        override val showCreateDialog: Boolean,
        val error: GenreError?
    ) : GenreSelectionUiState

    data class ShowList(
        val genres: List<String>,
        override val showCreateDialog: Boolean,
        val error: GenreError?,
    ) : GenreSelectionUiState
}

private data class GenreSelectionViewModelState(
    val loading: Boolean = true,
    val genres: List<String> = emptyList(),
    val showCreateDialog: Boolean = false,
    val error: GenreError? = null
) {
    fun toState(): GenreSelectionUiState =
        when {
            loading -> GenreSelectionUiState.Loading

            genres.isEmpty() ->
                GenreSelectionUiState.NoGenre(
                    showCreateDialog = showCreateDialog,
                    error = error,
                )

            else ->
                GenreSelectionUiState.ShowList(
                    genres = genres,
                    showCreateDialog = showCreateDialog,
                    error = error
                )
        }
}

sealed interface GenreSelectionEvent {
    data class Select(val genre: String) : GenreSelectionEvent
}

@HiltViewModel
class GenreSelectionViewModel @Inject constructor(
    private val getAllGenreNameStreamUseCase: GetAllGenreNameStreamUseCase,
    private val createGenreUseCase: CreateGenreUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(GenreSelectionViewModelState())
    val state: StateFlow<GenreSelectionUiState> = _state
        .map { it.toState() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = GenreSelectionUiState.Loading
        )

    private val _event = Channel<GenreSelectionEvent>(BUFFERED)
    val event: Flow<GenreSelectionEvent> = _event.receiveAsFlow()

    init {
        viewModelScope.launch {
            getAllGenreNameStreamUseCase().collect { genres ->
                _state.update {
                    it.copy(
                        loading = false,
                        genres = genres,
                        error = null,
                    )
                }
            }
        }
    }

    fun showCreateDialog() {
        _state.update { it.copy(showCreateDialog = true) }
    }

    fun dismissCreateDialog() {
        _state.update { it.copy(showCreateDialog = false) }
    }

    fun create(genre: String) {
        viewModelScope.launch {
            val result = createGenreUseCase(genre = genre)
            when {
                result.isFailure -> _state.update { it.copy(error = GenreError.Create()) }
                result.isSuccess -> {
                    dismissCreateDialog()
                    result.getOrNull()
                        ?.let(GenreSelectionEvent::Select)
                        ?.let { _event.send(it) }
                }
            }
        }
    }
}
