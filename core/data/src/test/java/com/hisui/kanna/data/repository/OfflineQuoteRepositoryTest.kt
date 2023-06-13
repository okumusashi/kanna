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

package com.hisui.kanna.data.repository

import com.google.common.truth.Truth.assertThat
import com.hisui.kanna.core.data.mapper.asExternalModel
import com.hisui.kanna.core.data.repository.OfflineQuoteRepository
import com.hisui.kanna.core.database.entity.AuthorEntity
import com.hisui.kanna.core.database.entity.BookAndAuthorEntity
import com.hisui.kanna.core.database.entity.BookReadStatusEntity
import com.hisui.kanna.core.database.entity.QuoteEntity
import com.hisui.kanna.data.testBookEntity
import com.hisui.kanna.data.testdoubles.FakeQuoteDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OfflineQuoteRepositoryTest {
    private lateinit var dao: FakeQuoteDao
    private lateinit var repository: OfflineQuoteRepository
    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeEach
    fun setup() {
        dao = FakeQuoteDao()
        repository = OfflineQuoteRepository(
            dao = dao,
            ioDispatcher = testDispatcher
        )
    }

    @Nested
    @DisplayName("#getStream")
    inner class GetStream {

        @Nested
        @DisplayName("GIVEN - arbitrary id")
        inner class Given {

            private val id = 1L

            @Test
            fun `WHEN - dao#getStream returns empty map, THEN - it should return null`() {
                // WHEN
                runTest {
                    assertThat(dao.getAll().first()).isEmpty()
                }

                // THEN
                runTest {
                    assertThat(repository.getStream(id = id).first()).isNull()
                }
            }

            @Test
            fun `WHEN = dao#gerStream returns value, THEN - it should return the data`() {
                // WHEN
                val bookId = 2L
                val bookAndAuthor = BookAndAuthorEntity(
                    book = testBookEntity(id = bookId),
                    author = AuthorEntity(name = "name", memo = null),
                    status = BookReadStatusEntity(id = 1, status = "status")
                )
                val entity = QuoteEntity(
                    _id = id,
                    bookId = bookId,
                    page = 10,
                    quote = "quote",
                    thought = "thought",
                    _createdAt = Clock.System.now()
                )

                runTest {
                    dao.addBookAndAuthor(bookAndAuthor = bookAndAuthor)
                    dao.insert(entity)
                }

                // THEN
                runTest {
                    val expected = entity.asExternalModel(bookAndAuthor)
                    assertThat(repository.getStream(id = id).first()).isEqualTo(expected)
                }
            }
        }
    }
}
