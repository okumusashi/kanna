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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class BookSelectionViewModel @Inject constructor(
    private val getFilteredBooksStreamUseCase: GetFilteredBooksStreamUseCase
) : ViewModel() {

    private val _bookCandidates = MutableStateFlow(emptyList<BookForQuote>())
    val bookCandidates: StateFlow<List<BookForQuote>> = _bookCandidates

    fun filterBooks(q: String) {
        if (q.isBlank()) {
            _bookCandidates.update { emptyList() }
        }

        viewModelScope.launch {
            getFilteredBooksStreamUseCase(q = q).collect { books ->
                _bookCandidates.update { books }
            }
        }
    }
}
