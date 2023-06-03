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
import com.hisui.kanna.core.domain.usecase.GetBookStreamUseCase
import com.hisui.kanna.core.model.Book
import com.hisui.kanna.feature.book.navigation.BookArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class BookViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getBookStreamUseCase: GetBookStreamUseCase
) : ViewModel() {

    private val args = BookArgs(savedStateHandle)

    private val _uiState = getBookStreamUseCase(id = args.bookId)
        .map { result ->
            when (result) {
                is Result.Success -> BookUiState.Success(result.data)
                is Result.Error -> BookUiState.Error(result.error)
                is Result.Loading -> BookUiState.Loading
            }
        }

    val uiState: StateFlow<BookUiState> = _uiState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            BookUiState.Loading
        )
}

sealed interface BookUiState {
    data class Success(val book: Book) : BookUiState
    data class Error(val error: KannaError) : BookUiState
    object Loading : BookUiState
}
