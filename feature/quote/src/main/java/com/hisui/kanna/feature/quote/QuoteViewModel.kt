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

package com.hisui.kanna.feature.quote

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hisui.kanna.core.data.repository.QuoteRepository
import com.hisui.kanna.core.domain.usecase.CountBooksStreamUseCase
import com.hisui.kanna.core.model.Quote
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface QuoteUiState {
    object Loading : QuoteUiState
    object NoBook : QuoteUiState
    object NoQuote : QuoteUiState
    data class ShowQuotes(val quotes: List<Quote>) : QuoteUiState
}

private data class QuoteViewModelState(
    val quotes: List<Quote> = emptyList(),
    val hasBook: Boolean = true,
    val loading: Boolean = true
) {
    fun toUiState(): QuoteUiState =
        when {
            loading -> QuoteUiState.Loading
            !hasBook -> QuoteUiState.NoBook
            quotes.isEmpty() -> QuoteUiState.NoQuote
            else -> QuoteUiState.ShowQuotes(quotes = quotes)
        }
}

@HiltViewModel
class QuoteViewModel @Inject constructor(
    repository: QuoteRepository,
    countBooksStreamUseCase: CountBooksStreamUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuoteViewModelState())
    val uiState = _uiState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            QuoteUiState.Loading
        )

    init {
        viewModelScope.launch {
            launch {
                repository.getAllStream().collect { quotes ->
                    _uiState.update { it.copy(loading = false, quotes = quotes) }
                }
            }
            launch {
                countBooksStreamUseCase().collect { count ->
                    _uiState.update { it.copy(hasBook = count > 0) }
                }
            }
        }
    }
}
