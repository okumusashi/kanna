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

import com.google.common.truth.Truth.assertThat
import com.hisui.kanna.core.domain.usecase.GetFilteredBooksStreamUseCase
import com.hisui.kanna.core.testing.MainDispatcherExtension
import com.hisui.kanna.core.testing.data.asForm
import com.hisui.kanna.core.testing.data.defaultBook
import com.hisui.kanna.core.testing.repository.TestBookRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MainDispatcherExtension::class)
class BookSelectionViewModelTest {

    private val bookRepository = TestBookRepository()

    private val getFilteredBooksStreamUseCase = GetFilteredBooksStreamUseCase(
        repository = bookRepository
    )

    private lateinit var viewModel: BookSelectionViewModel

    @BeforeEach
    fun setup() {
        viewModel = BookSelectionViewModel(
            getFilteredBooksStreamUseCase = getFilteredBooksStreamUseCase
        )
    }

    @Nested
    @DisplayName("#filterBooks")
    inner class FilterBooks {

        @Nested
        @DisplayName("GIVEN - q is blank")
        inner class Given1 {

            private val q = ""

            @Test
            fun `WHEN - bookCandidate is already set, THEN - it should empty the list and title should be empty`() {
                runTest {
                    val title = "aa"
                    bookRepository.save(defaultBook.copy(title = title).asForm())
                    viewModel.filterBooks(q = "aa")

                    // WHEN - `bookCandidates` is already set
                    assertThat(viewModel.uiState.first().bookCandidates).isNotEmpty()
                }

                runTest {
                    // THEN
                    viewModel.filterBooks(q = q)

                    val result = viewModel.uiState.first()
                    assertThat(result.bookCandidates).isEmpty()
                    assertThat(result.title).isEmpty()
                }
            }
        }

        @Nested
        @DisplayName("GIVEN - q is not blank")
        inner class Given2 {

            private val q = "a"

            @Test
            fun `WHEN - q hits a book, THEN - it should return bookCandidates containing the book, and set q as the title`() {
                runTest {
                    // WHEN
                    val title = "ab"
                    bookRepository.save(defaultBook.copy(title = title).asForm())

                    // THEN
                    viewModel.filterBooks(q = q)
                    val uiState = viewModel.uiState.first()
                    assertThat(uiState.bookCandidates).isNotEmpty()
                }
            }
        }
    }
}
