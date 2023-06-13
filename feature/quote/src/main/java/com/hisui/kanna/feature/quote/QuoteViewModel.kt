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
import com.hisui.kanna.core.domain.usecase.GetQuoteStreamUseCase
import com.hisui.kanna.core.model.Quote
import com.hisui.kanna.feature.quote.navigation.QuoteArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class QuoteViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getQuoteStreamUseCase: GetQuoteStreamUseCase
) : ViewModel() {

    private val args = QuoteArgs(savedStateHandle)

    private val _uiState = getQuoteStreamUseCase(id = args.quoteId)
        .map { result ->
            when (result) {
                is Result.Success -> QuoteUiState.ShowQuote(quote = result.data)
                is Result.Error -> QuoteUiState.Error(error = result.error)
                is Result.Loading -> QuoteUiState.Loading
            }
        }

    val uiState: StateFlow<QuoteUiState> = _uiState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            QuoteUiState.Loading
        )
}

sealed interface QuoteUiState {
    data class ShowQuote(val quote: Quote) : QuoteUiState
    data class Error(val error: KannaError) : QuoteUiState
    object Loading : QuoteUiState
}
