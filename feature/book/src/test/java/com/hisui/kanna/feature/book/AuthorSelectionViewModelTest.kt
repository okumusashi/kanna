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
import com.hisui.kanna.core.domain.usecase.CreateAuthorUseCase
import com.hisui.kanna.core.domain.usecase.GetAllAuthorsStreamUseCase
import com.hisui.kanna.core.testing.MainDispatcherExtension
import com.hisui.kanna.core.testing.repository.TestAuthorRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MainDispatcherExtension::class)
class AuthorSelectionViewModelTest {

    private val repository = TestAuthorRepository()

    private val createAuthorUseCase = CreateAuthorUseCase(repository = repository)
    private val getAllAuthorsStreamUseCase = GetAllAuthorsStreamUseCase(repository = repository)

    private lateinit var viewModel: AuthorSelectionViewModel

    @BeforeEach
    fun setup() {
        viewModel = AuthorSelectionViewModel(
            createAuthorUseCase = createAuthorUseCase,
            getAllAuthorsStreamUseCase = getAllAuthorsStreamUseCase
        )
    }

    @Nested
    @DisplayName("#createAuthor")
    inner class CreateAuthor {

        @BeforeEach
        fun setup() {
            // Dialog should be shown before calling #createAuthor
            viewModel.showCreateDialog()
        }

        @Nested
        @DisplayName("GIVEN - arbitrary name and memo")
        inner class Given1 {

            private val name = "name"
            private val memo = "memo"

            @Test
            fun `WHEN - createAuthorUseCase fails, THEN - it should update the error`() {
                // Seems like it never fails.
            }

            @Test
            fun `WHEN - createAuthorUseCase succeeds, THEN - it should dismissCreateDialog and send AuthorSelectionEvent-Select event`() {
                runTest {
                    viewModel.createAuthor(name = name, memo = memo)

                    val state = viewModel.state.first() as AuthorSelectionUiState.ShowList
                    assertThat(state.showCreateDialog).isFalse()

                    (viewModel.event.first() as AuthorSelectionEvent.Select).author.let { author ->
                        assertThat(author.name).isEqualTo(name)
                        assertThat(author.memo).isEqualTo(memo)
                    }
                }
            }
        }
    }
}
