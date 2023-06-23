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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hisui.kanna.core.domain.error.QuoteError
import com.hisui.kanna.core.model.DEFAULT_BOOK_ID
import com.hisui.kanna.core.model.QuoteField
import com.hisui.kanna.core.model.QuoteForm
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.BUFFERED
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class QuoteFormUiState(
    val hasBeenFocused: Map<QuoteField, Boolean> = QuoteField.values().associateWith { false },
    val errors: Map<QuoteField, QuoteError.Validation?> = QuoteField.values().associateWith { null }
) {
    val submittable: Boolean
        get() = errors.values.all { it == null } && hasBeenFocused.values.any { it }
}

sealed interface QuoteFormEvent {
    data class Validation(val field: QuoteField) : QuoteFormEvent
}

class QuoteFormViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(QuoteFormUiState())
    val uiState: StateFlow<QuoteFormUiState> = _uiState

    private val _event = Channel<QuoteFormEvent>(BUFFERED)
    val event: Flow<QuoteFormEvent> = _event.receiveAsFlow()

    fun focused(field: QuoteField) {
        _uiState.update {
            it.copy(hasBeenFocused = it.hasBeenFocused + (field to true))
        }
    }

    fun askValidation(field: QuoteField) {
        viewModelScope.launch {
            _event.send(QuoteFormEvent.Validation(field))
        }
    }

    fun validate(field: QuoteField, form: QuoteForm) {
        when (field) {
            QuoteField.QUOTE -> validateQuote(form.quote)
            QuoteField.PAGE -> validatePage(form.page)
            QuoteField.BOOK -> validateBook(form.bookId)
            else -> { /*TODO*/ }
        }
    }

    fun validateQuote(value: String = "") {
        _uiState.update { state ->
            if (state.hasBeenFocused[QuoteField.QUOTE] == false) {
                return@update state
            }

            if (value.isBlank()) {
                QuoteError.Validation.Required
            } else {
                null
            }.let {
                state.copy(errors = state.errors + (QuoteField.QUOTE to it))
            }
        }
    }

    /**
     * [value] can be negative, and it should be invalid in general.
     * But we can't be 100% sure if there is any tricky book with negative page number or not.
     */
    fun validatePage(value: Int?) {
        _uiState.update { state ->
            if (state.hasBeenFocused[QuoteField.PAGE] == false) {
                return@update state
            }

            if (value == null) {
                QuoteError.Validation.Required
            } else {
                null
            }.let {
                state.copy(errors = state.errors + (QuoteField.PAGE to it))
            }
        }
    }

    fun validateBook(value: Long, skipFocusCheck: Boolean = false) {
        _uiState.update { state ->
            if (!skipFocusCheck && state.hasBeenFocused[QuoteField.BOOK] == false) {
                return@update state
            }

            if (value == DEFAULT_BOOK_ID) {
                QuoteError.Validation.Required
            } else {
                null
            }.let {
                state.copy(errors = state.errors + (QuoteField.BOOK to it))
            }
        }
    }
}
