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
import com.hisui.kanna.core.domain.error.QuoteError
import com.hisui.kanna.core.model.NewQuote
import com.hisui.kanna.core.testing.data.defaultBook
import com.hisui.kanna.core.testing.repository.TestQuoteRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetQuoteStreamUseCaseTest {

    private val repository = TestQuoteRepository()

    private val useCase = GetQuoteStreamUseCase(repository = repository)

    @Nested
    @DisplayName("GIVEN - arbitrary id")
    inner class Given {

        private val id = 1L

        @Test
        fun `WHEN - repository#getStream returns null, THEN - it shuold return NotFound`() {
            runTest {
                val quotes = repository.getAllStream().first()
                assertThat(quotes).isEmpty()
            }

            runTest {
                val expected = QuoteError.NotFound
                val result = useCase(id = id).first() as Result.Error
                assertThat(result.error).isEqualTo(expected)
            }
        }

        @Test
        fun `WHEN - repository#getStream returns a quote, THEN - it should success with the data`() {
            val book = defaultBook.copy(id = id)
            repository.addBook(book = book)

            val quote = NewQuote(
                page = 1,
                quote = "quote",
                thought = "thought",
                bookId = book.id
            )

            runTest {
                // This is the first item so the id should be 1L
                repository.save(quote)

                val result = useCase(id = id).first() as Result.Success
                assertThat(result.data.quote).isEqualTo(quote.quote)
            }
        }
    }
}
