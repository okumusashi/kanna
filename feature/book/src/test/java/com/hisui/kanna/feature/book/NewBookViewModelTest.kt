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

import com.google.common.truth.Truth.assertThat
import com.hisui.kanna.core.domain.usecase.GetAllStatusUseCase
import com.hisui.kanna.core.model.BookForm
import com.hisui.kanna.core.testing.MainDispatcherExtension
import com.hisui.kanna.core.testing.repository.TestBookReadStatusRepository
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
class NewBookViewModelTest {

    private val bookRepository = TestBookRepository()

    private val getAllStatusUseCase = GetAllStatusUseCase(
        repository = TestBookReadStatusRepository()
    )

    private lateinit var viewModel: NewBookViewModel

    @BeforeEach
    fun setup() {
        viewModel = NewBookViewModel(
            bookRepository = bookRepository,
            getAllStatusUseCase = getAllStatusUseCase
        )
    }

    @Nested
    @DisplayName("initial state")
    inner class InitialState {

        @Test
        fun `WHEN - NewBookViewModel is initiated, THEN - it should set statuses`() {
            // Already initialed in `@BeforeEach` block
            runTest {
                val statuses = getAllStatusUseCase()
                assertThat(viewModel.uiState.first().statuses).isEqualTo(statuses)
            }
        }
    }

    @Nested
    @DisplayName("#createBook")
    inner class CreateBook {

        @Test
        fun `WHEN - repository#save fails, THEN - it should update error in state`() {
            // It actually never happens
        }

        @Test
        fun `WHEN - repository#save succeeds, THEN - it should send Created event`() {
            val newBook = BookForm(
                title = "title",
                readDate = Instant.parse("2022-01-01T00:00:00Z"),
                authorId = "author",
                genreId = "genre",
                thought = "thought",
                memo = null,
                rating = 2,
                statusId = 1
            )
            runTest {
                viewModel.createBook(book = newBook)

                val event = viewModel.event.first()
                assertThat(event).isInstanceOf(NewBookEvent.Created::class.java)
            }
        }
    }
}
