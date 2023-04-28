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

package com.hisui.kanna.feature.home.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hisui.kanna.core.data.repository.BookRepository
import com.hisui.kanna.core.model.Book
import com.hisui.kanna.core.model.BookSorter
import com.hisui.kanna.core.model.Sort
import com.hisui.kanna.core.model.SortDirection
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

    private suspend fun getRecentBooks(sort: Sort) {
        bookRepository.getAllStream(sort = sort).collect { books ->
            _uiState.update {
                it.copy(books = books, loading = false)
            }
        }
    }

    fun sortByReadDateDesc() {
        viewModelScope.launch {
            val sort = Sort(
                by = BookSorter.READ_DATE,
                direction = SortDirection.DESC
            )
            getRecentBooks(sort = sort)
        }
    }

    fun sortByReadDateAsc() {
        viewModelScope.launch {
            val sort = Sort(
                by = BookSorter.READ_DATE,
                direction = SortDirection.ASC
            )
            getRecentBooks(sort = sort)
        }
    }

    fun sortByTitleAsc() {
        viewModelScope.launch {
            val sort = Sort(
                by = BookSorter.TITLE,
                direction = SortDirection.ASC
            )
            getRecentBooks(sort = sort)
        }
    }

    fun sortByTitleDesc() {
        viewModelScope.launch {
            val sort = Sort(
                by = BookSorter.TITLE,
                direction = SortDirection.DESC
            )
            getRecentBooks(sort = sort)
        }
    }
}
