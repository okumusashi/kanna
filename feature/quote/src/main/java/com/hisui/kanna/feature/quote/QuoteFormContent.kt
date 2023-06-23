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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hisui.kanna.core.domain.error.QuoteError
import com.hisui.kanna.core.model.BookForQuote
import com.hisui.kanna.core.model.QuoteField
import com.hisui.kanna.core.model.QuoteForm
import com.hisui.kanna.core.ui.component.KannaTopBar
import com.hisui.kanna.core.ui.preview.PreviewColumnWrapper

@Composable
internal fun QuoteFormBase(
    modifier: Modifier = Modifier,
    isCompact: Boolean,
    title: String,
    submitButtonTitle: String,
    onSubmit: () -> Unit,
    submittable: Boolean,
    onExit: () -> Unit,
    content: @Composable (paddingValues: PaddingValues) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = modifier
                .align(Alignment.Center)
                .fillMaxSize(if (isCompact) 1f else 0.65f),
            topBar = {
                KannaTopBar(
                    title = title,
                    submitButtonTitle = submitButtonTitle,
                    onClickNavigationIcon = onExit,
                    onSubmit = onSubmit,
                    enabled = submittable
                )
            }
        ) { paddingValues ->
            content(paddingValues)
        }
    }
}

@Composable
internal fun QuoteError.Validation.message(field: QuoteField): String =
    when (this) {
        QuoteError.Validation.Required ->
            stringResource(id = com.hisui.kanna.core.ui.R.string.required_field, field.title())
    }

@Composable
private fun QuoteField.title(): String =
    when (this) {
        QuoteField.QUOTE -> stringResource(id = com.hisui.kanna.core.ui.R.string.quote)
        QuoteField.BOOK -> stringResource(id = com.hisui.kanna.core.ui.R.string.book)
        QuoteField.PAGE -> stringResource(id = R.string.page)
        QuoteField.THOUGHT -> stringResource(id = com.hisui.kanna.core.ui.R.string.thought)
    }

@Composable
internal fun QuoteFormContent(
    modifier: Modifier = Modifier,
    viewModel: QuoteFormViewModel = viewModel(),
    quoteForm: QuoteForm,
    selectedBookTitle: String? = null,
    onUpdateQuote: (QuoteForm) -> Unit,
    onSelectBook: (BookForQuote) -> Unit,
    onSubmittableChange: (Boolean) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(quoteForm) {
        viewModel.event.collect { event ->
            when (event) {
                is QuoteFormEvent.Validation -> {
                    viewModel.validate(field = event.field, form = quoteForm)
                }
            }
        }
    }

    LaunchedEffect(uiState.submittable) {
        onSubmittableChange(uiState.submittable)
    }

    LazyColumn(
        modifier = modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            QuoteSection(
                quote = quoteForm.quote,
                error = uiState.errors[QuoteField.QUOTE],
                onFocusChanged = {
                    if (it.isFocused) {
                        viewModel.focused(QuoteField.QUOTE)
                    } else if (uiState.hasBeenFocused[QuoteField.QUOTE] == true) {
                        viewModel.askValidation(QuoteField.QUOTE)
                    }
                },
                onValueChange = {
                    onUpdateQuote(quoteForm.copy(quote = it))
                    viewModel.validateQuote(it)
                }
            )
        }

        item {
            BookSelection(
                modifier = Modifier.onFocusChanged { viewModel.focused(QuoteField.BOOK) },
                initial = selectedBookTitle ?: "",
                error = uiState.errors[QuoteField.BOOK],
                onFocusChanged = {
                    if (it.isFocused) {
                        viewModel.focused(QuoteField.BOOK)
                    } else if (uiState.hasBeenFocused[QuoteField.BOOK] == true) {
                        viewModel.askValidation(QuoteField.BOOK)
                    }
                },
                onSelect = { book ->
                    onSelectBook(book)
                    onUpdateQuote(quoteForm.copy(bookId = book.id))
                    viewModel.validateBook(value = book.id, skipFocusCheck = true)
                }
            )
        }

        item {
            PageSection(
                page = quoteForm.page?.toString() ?: "",
                error = uiState.errors[QuoteField.PAGE],
                onFocusChanged = {
                    if (it.isFocused) {
                        viewModel.focused(QuoteField.PAGE)
                    } else if (uiState.hasBeenFocused[QuoteField.PAGE] == true) {
                        viewModel.askValidation(QuoteField.PAGE)
                    }
                },
                onValueChange = {
                    onUpdateQuote(quoteForm.copy(page = it.toIntOrNull()))
                    viewModel.validatePage(it.toIntOrNull())
                }
            )
        }

        item {
            // Thought section
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .onFocusChanged { viewModel.focused(QuoteField.THOUGHT) },
                value = quoteForm.thought,
                onValueChange = { onUpdateQuote(quoteForm.copy(thought = it)) },
                label = { Text(text = stringResource(id = com.hisui.kanna.core.ui.R.string.thought) + stringResource(id = com.hisui.kanna.core.ui.R.string.optional)) },
                keyboardOptions = KeyboardOptions(KeyboardCapitalization.Sentences)
            )
        }

        item { Spacer(modifier = Modifier.height(64.dp)) }
    }
}

@Preview
@Composable
private fun QuoteFormContentPreview() {
    PreviewColumnWrapper {
        QuoteFormContent(
            modifier = Modifier.padding(32.dp),
            quoteForm = QuoteForm(quote = "", thought = "", bookId = 1L, page = 1),
            onUpdateQuote = {},
            onSelectBook = {},
            onSubmittableChange = {}
        )
    }
}

@Composable
private fun QuoteSection(
    quote: String,
    error: QuoteError.Validation?,
    onFocusChanged: (FocusState) -> Unit,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        modifier = Modifier
            .onFocusChanged(onFocusChanged)
            .fillMaxWidth()
            .height(200.dp),
        value = quote,
        onValueChange = onValueChange,
        isError = error != null,
        supportingText = {
            when (error) {
                is QuoteError.Validation.Required ->
                    Text(text = error.message(QuoteField.QUOTE))

                else -> { /**/ }
            }
        },
        label = { Text(text = stringResource(id = R.string.quote)) },
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
    )
}

@Composable
private fun PageSection(
    page: String,
    error: QuoteError.Validation?,
    onFocusChanged: (FocusState) -> Unit,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged(onFocusChanged),
        singleLine = true,
        value = page,
        onValueChange = onValueChange,
        isError = error != null,
        supportingText = {
            when (error) {
                is QuoteError.Validation.Required ->
                    Text(text = error.message(QuoteField.PAGE))

                else -> { /**/ }
            }
        },
        label = { Text(text = stringResource(id = R.string.page)) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
}
