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
import com.hisui.kanna.core.domain.error.QuoteError
import com.hisui.kanna.core.model.QuoteField
import com.hisui.kanna.core.testing.MainDispatcherExtension
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MainDispatcherExtension::class)
class QuoteFormViewModelTest {

    private lateinit var viewModel: QuoteFormViewModel

    @BeforeEach
    fun setup() {
        viewModel = QuoteFormViewModel()
    }

    @Nested
    @DisplayName("#validateQuote")
    inner class ValidateQuote {

        @Nested
        @DisplayName("GIVEN - empty value")
        inner class Given1 {

            private val value = ""

            @Test
            fun `WHEN - QUOTE field has NOT been focused, THEN - it should do nothing`() {
                // WHEN
                QuoteField.QUOTE.hasNotBeenFocused()

                // THEN
                runTest {
                    val initialState = viewModel.uiState.first()
                    viewModel.validateQuote(value = value)
                    assertThat(viewModel.uiState.first()).isEqualTo(initialState)
                }
            }

            @Test
            fun `WHEN - QUOTE field has been focused, THEN - it should return Required error`() {
                // WHEN
                QuoteField.QUOTE.hasBeenFocused()

                // THEN
                runTest {
                    viewModel.validateQuote(value = value)

                    val actual = viewModel.uiState.first().errors[QuoteField.QUOTE]
                    assertThat(actual).isEqualTo(QuoteError.Validation.Required)
                }
            }
        }

        @Nested
        @DisplayName("GIVEN - non-empty value")
        inner class Given2 {

            private val value = "abc"

            @Test
            fun `WHEN - QUOTE field has focused, THEN - QUOTE error should be null`() {
                // WHEN
                QuoteField.QUOTE.hasBeenFocused()

                // THEN
                runTest {
                    viewModel.validateQuote(value = value)
                    assertThat(viewModel.uiState.first().errors[QuoteField.QUOTE]).isNull()
                }
            }
        }
    }

    @Nested
    @DisplayName("#validatePage")
    inner class ValidatePage {

        @Nested
        @DisplayName("GIVEN - null")
        inner class Given1 {

            private val value = null

            @Test
            fun `WHEN - PAGE has NOT been focused, THEN - it should do nothing`() {
                // WHEN
                QuoteField.PAGE.hasNotBeenFocused()

                // THEN
                runTest {
                    val initialState = viewModel.uiState.first()
                    viewModel.validatePage(value = value)
                    assertThat(viewModel.uiState.first()).isEqualTo(initialState)
                }
            }

            @Test
            fun `WHEN - PAGE has been focused, THEN - it should return Required error`() {
                // WHEN
                QuoteField.PAGE.hasBeenFocused()

                // THEN
                runTest {
                    viewModel.validatePage(value = value)

                    val actual = viewModel.uiState.first().errors[QuoteField.PAGE]
                    assertThat(actual).isEqualTo(QuoteError.Validation.Required)
                }
            }
        }

        @Nested
        @DisplayName("GIVEN - arbitrary Int")
        inner class Given2 {

            private val value = 1

            @Test
            fun `WHEN - PAGE has focused, THEN - PAGE error should be null`() {
                // WHEN
                QuoteField.PAGE.hasBeenFocused()

                // THEN
                runTest {
                    viewModel.validatePage(value = value)
                    assertThat(viewModel.uiState.first().errors[QuoteField.PAGE]).isNull()
                }
            }
        }
    }

    private fun QuoteField.hasNotBeenFocused() = runTest {
        val field = this@hasNotBeenFocused
        assertThat(viewModel.uiState.first().hasBeenFocused[field]).isFalse()
    }

    private fun QuoteField.hasBeenFocused() = runTest {
        val field = this@hasBeenFocused
        viewModel.focused(field = field)
        assertThat(viewModel.uiState.first().hasBeenFocused[field]).isTrue()
    }
}
