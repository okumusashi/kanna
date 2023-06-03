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

package com.hisui.kanna.core.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.hisui.kanna.core.Result
import com.hisui.kanna.core.domain.error.BookError
import com.hisui.kanna.core.model.BookSorter
import com.hisui.kanna.core.testing.data.asNewBook
import com.hisui.kanna.core.testing.data.defaultBook
import com.hisui.kanna.core.testing.repository.TestBookRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetBookStreamUseCaseTest {

    private val repository = TestBookRepository()

    private val useCase = GetBookStreamUseCase(repository = repository)

    @Nested
    @DisplayName("GIVEN - arbitrary id")
    inner class Given {
        private val id = 1L

        @Test
        fun `WHEN - repository#getStream returns null, THEN - it should return NotFound`() {
            runTest {
                val books = repository.getAllStream(sort = BookSorter.READ_DATE, isAsc = false).first()
                assertThat(books).isEmpty()
            }

            runTest {
                val expected = BookError.NotFound
                val result = useCase(id = id).first() as Result.Error
                assertThat(result.error).isEqualTo(expected)
            }
        }

        @Test
        fun `WHEN - repository#getStream returns a book, THEN - it should success with the data`() {
            val book = defaultBook.copy(id = id)
            runTest {
                repository.save(book = book.asNewBook())
            }

            runTest {
                val result = useCase(id = id).first() as Result.Success
                assertThat(result.data).isEqualTo(book)
            }
        }
    }
}
