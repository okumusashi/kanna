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
import com.hisui.kanna.core.data.mapper.asEntity
import com.hisui.kanna.core.data.mapper.asExternalModel
import com.hisui.kanna.core.data.repository.OfflineAuthorRepository
import com.hisui.kanna.core.model.AuthorInput
import com.hisui.kanna.data.testdoubles.FakeAuthorDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OfflineAuthorRepositoryTest {

    private lateinit var dao: FakeAuthorDao
    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var repository: OfflineAuthorRepository

    @BeforeEach
    fun setup() {
        dao = FakeAuthorDao()

        repository = OfflineAuthorRepository(
            dao = dao,
            ioDispatcher = testDispatcher
        )
    }

    @Nested
    @DisplayName("#save")
    inner class Save {

        @Nested
        @DisplayName("GIVEN - arbitrary authorInput")
        inner class Given1 {

            private val author = AuthorInput(name = "name", memo = "memo")

            @Test
            fun `WHEN - no exception is thrown, THEN - it should save entity and return as external model`() {
                runTest {
                    assertThat(dao.getAllStream().first()).hasSize(0)
                }

                runTest {
                    val result = repository.save(author = author)

                    assertThat(dao.getAllStream().first()).hasSize(1)
                    assertThat(result.getOrNull()!!).isEqualTo(author.asEntity().asExternalModel())
                }
            }
        }
    }
}
