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

import com.google.common.truth.Truth.assertThat
import com.hisui.kanna.core.testing.MainDispatcherExtension
import com.hisui.kanna.core.testing.data.asNewBook
import com.hisui.kanna.core.testing.data.defaultBook
import com.hisui.kanna.core.testing.repository.TestBookRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MainDispatcherExtension::class)
class HomeViewModelTest {

    private val bookRepository = TestBookRepository()

    private lateinit var viewModel: HomeViewModel

    private val bookList =
        listOf(
            defaultBook.copy(id = 1, title = "aa", readDate = Instant.parse("2022-01-01T01:00:00Z")),
            defaultBook.copy(id = 2, title = "bb", readDate = Instant.parse("2023-01-01T01:00:00Z")),
            defaultBook.copy(id = 3, title = "cc", readDate = Instant.parse("2021-01-01T01:00:00Z"))
        )

    @BeforeEach
    fun setup() {
        runTest {
            bookList
                .map { it.asNewBook() }
                .forEach { bookRepository.save(it) }
        }

        viewModel = HomeViewModel(bookRepository = bookRepository)
    }

    @Nested
    @DisplayName("#sortByReadDesc")
    inner class SortByReadDesc {
        @Test
        fun `GIVEN - nothing, WHEN - it is called, THEN - it should update the order`() {
            runTest {
                viewModel.sortByReadDateDesc()
            }

            runTest {
                val expected = bookList.sortedByDescending { it.readDate }
                val actual = (viewModel.uiState.first() as HomeUiState.RecentBooks).books
                assertThat(actual)
                    .containsExactlyElementsIn(expected)
                    .inOrder()
            }
        }
    }

    @Nested
    @DisplayName("#sortByReadAsc")
    inner class SortByReadAsc {
        @Test
        fun `GIVEN - nothing, WHEN - it is called, THEN - it should update the order`() {
            runTest {
                viewModel.sortByReadDateAsc()
            }

            runTest {
                val expected = bookList.sortedBy { it.readDate }
                val actual = (viewModel.uiState.first() as HomeUiState.RecentBooks).books
                assertThat(actual)
                    .containsExactlyElementsIn(expected)
                    .inOrder()
            }
        }
    }

    @Nested
    @DisplayName("#sortByTitleDesc")
    inner class SortByTitleDesc {
        @Test
        fun `GIVEN - nothing, WHEN - it is called, THEN - it should update the order`() {
            runTest {
                viewModel.sortByTitleDesc()
            }

            runTest {
                val expected = bookList.sortedByDescending { it.title }
                val actual = (viewModel.uiState.first() as HomeUiState.RecentBooks).books
                assertThat(actual)
                    .containsExactlyElementsIn(expected)
                    .inOrder()
            }
        }
    }

    @Nested
    @DisplayName("#sortByTitleAsc")
    inner class SortByTitleAsc {
        @Test
        fun `GIVEN - nothing, WHEN - it is called, THEN - it should update the order`() {
            runTest {
                viewModel.sortByTitleAsc()
            }

            runTest {
                val expected = bookList.sortedBy { it.title }
                val actual = (viewModel.uiState.first() as HomeUiState.RecentBooks).books
                assertThat(actual)
                    .containsExactlyElementsIn(expected)
                    .inOrder()
            }
        }
    }
}
