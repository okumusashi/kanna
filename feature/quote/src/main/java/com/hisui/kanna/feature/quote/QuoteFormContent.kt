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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hisui.kanna.core.model.BookForQuote
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
internal fun QuoteFormContent(
    modifier: Modifier = Modifier,
    quoteForm: QuoteForm,
    selectedBookTitle: String? = null,
    onUpdateQuote: (QuoteForm) -> Unit,
    onSelectBook: (BookForQuote) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                value = quoteForm.quote,
                onValueChange = { onUpdateQuote(quoteForm.copy(quote = it)) },
                label = { Text(text = stringResource(id = R.string.quote)) },
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
            )
        }

        item {
            BookSelection(
                initial = selectedBookTitle ?: "",
                onSelect = { book ->
                    onSelectBook(book)
                    onUpdateQuote(quoteForm.copy(bookId = book.id))
                }
            )
        }

        item {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = quoteForm.page?.toString() ?: "",
                onValueChange = { onUpdateQuote(quoteForm.copy(page = it.toIntOrNull())) },
                label = { Text(text = stringResource(id = R.string.page)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }

        item {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                value = quoteForm.thought,
                onValueChange = { onUpdateQuote(quoteForm.copy(thought = it)) },
                label = { Text(text = stringResource(id = com.hisui.kanna.core.ui.R.string.thought)) },
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
            onSelectBook = {}
        )
    }
}
