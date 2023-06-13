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

package com.hisui.kanna.data.repository

import com.google.common.truth.Truth.assertThat
import com.hisui.kanna.core.data.mapper.asExternalModel
import com.hisui.kanna.core.data.repository.OfflineBookRepository
import com.hisui.kanna.core.database.dao.BookDao
import com.hisui.kanna.core.model.BookSorter
import com.hisui.kanna.data.testBookEntity
import com.hisui.kanna.data.testdoubles.FakeBookDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OfflineBookRepositoryTest {

    private lateinit var dao: BookDao
    private lateinit var repository: OfflineBookRepository
    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeEach
    fun setup() {
        dao = FakeBookDao()
        repository = OfflineBookRepository(
            dao = dao,
            ioDispatcher = testDispatcher
        )
    }

    @Nested
    @DisplayName("#getAllStream")
    inner class GetAllStream {

        @Nested
        @DisplayName("GIVEN - sort = TITLE, isAsc = true")
        inner class Given1 {

            private val sort = BookSorter.TITLE
            private val isAsc = true

            @Test
            fun `WHEN - 2 bookEntities exist, THEN - it should get data by dao#getAllBooksAndAuthorsByTitle with isAsc = true`() {
                // WHEN
                runTest {
                    dao.insert(
                        testBookEntity(id = 1, title = "a"),
                        testBookEntity(id = 2, title = "b")
                    )
                }

                // THEN
                runTest {
                    assertThat(
                        repository
                            .getAllStream(sort = sort, isAsc = isAsc)
                            .first()
                    ).containsExactlyElementsIn(
                        dao.getAllBooksAndAuthorsByTitle(isAsc = isAsc)
                            .map { it.asExternalModel() }
                            .first()
                    ).inOrder()
                }
            }
        }

        @Nested
        @DisplayName("GIVEN - sort = READ_DATE, isAsc = true")
        inner class Given2 {

            private val sort = BookSorter.READ_DATE
            private val isAsc = true

            @Test
            fun `WHEN - 2 bookEntities exist, THEN - it should date by dao#getAllBooksAndAuthorsByReadDate with isAsc = true`() {
                // WHEN
                runTest {
                    dao.insert(
                        testBookEntity(id = 1, readDate = Instant.parse("2022-01-01T01:00:00Z")),
                        testBookEntity(id = 2, readDate = Instant.parse("2023-01-01T01:00:00Z"))
                    )
                }

                // THEN
                runTest {
                    assertThat(
                        repository
                            .getAllStream(sort = sort, isAsc = isAsc)
                            .first()
                    ).containsExactlyElementsIn(
                        dao.getAllBooksAndAuthorsByTitle(isAsc = isAsc)
                            .map { it.asExternalModel() }
                            .first()
                    ).inOrder()
                }
            }
        }
    }
}
