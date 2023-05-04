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

package com.hisui.kanna.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hisui.kanna.core.data.repository.BookRepository
import com.hisui.kanna.core.model.Book
import com.hisui.kanna.core.model.BookSorter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface HomeUiState {
    object Loading : HomeUiState
    object Empty : HomeUiState
    data class RecentBooks(val books: List<Book>) : HomeUiState
}

private data class HomeViewModelState(
    val books: List<Book> = emptyList(),
    val loading: Boolean = true
) {
    fun toUiState(): HomeUiState =
        when {
            loading -> HomeUiState.Loading
            books.isEmpty() -> HomeUiState.Empty
            else -> HomeUiState.RecentBooks(books = books)
        }
}

@HiltViewModel
internal class HomeViewModel @Inject constructor(
    private val bookRepository: BookRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeViewModelState())
    val uiState = _uiState
        .map { it.toUiState() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = HomeUiState.Loading
        )

    init {
        sortByReadDateDesc()
    }

    private suspend fun getRecentBooks(sort: BookSorter, isAsc: Boolean) {
        bookRepository.getAllStream(sort = sort, isAsc = isAsc).collect { books ->
            _uiState.update {
                it.copy(books = books, loading = false)
            }
        }
    }

    fun sortByReadDateDesc() {
        viewModelScope.launch {
            getRecentBooks(
                sort = BookSorter.READ_DATE,
                isAsc = false
            )
        }
    }

    fun sortByReadDateAsc() {
        viewModelScope.launch {
            getRecentBooks(
                sort = BookSorter.READ_DATE,
                isAsc = true
            )
        }
    }

    fun sortByTitleDesc() {
        viewModelScope.launch {
            getRecentBooks(
                sort = BookSorter.TITLE,
                isAsc = true
            )
        }
    }

    fun sortByTitleAsc() {
        viewModelScope.launch {
            getRecentBooks(
                sort = BookSorter.TITLE,
                isAsc = true
            )
        }
    }
}
