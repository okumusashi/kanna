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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    bookCandidates: List<BookForQuote>,
    onUpdateQuote: (QuoteForm) -> Unit,
    onUpdateBookFilter: (q: String) -> Unit,
    onSelectBook: (BookForQuote) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
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
                filteredBooks = bookCandidates,
                onUpdateFilter = onUpdateBookFilter,
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
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BookSelection(
    modifier: Modifier = Modifier,
    filteredBooks: List<BookForQuote>,
    onUpdateFilter: (q: String) -> Unit,
    onSelect: (BookForQuote) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }

    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            modifier = Modifier.menuAnchor(),
            value = title,
            onValueChange = {
                onUpdateFilter(it)
                title = it
            },
            label = { Text(stringResource(id = com.hisui.kanna.core.ui.R.string.book_title)) }
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            filteredBooks
                .ifEmpty {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = stringResource(id = R.string.type_to_search),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        onClick = { }
                    )
                    return@ExposedDropdownMenu
                }
                .forEach { item ->
                    DropdownMenuItem(
                        text = { Text(item.title) },
                        onClick = {
                            onSelect(item)
                            title = item.title
                            expanded = false
                        }
                    )
                }
        }
    }
}

@Preview
@Composable
private fun QuoteFormContentPreview() {
    PreviewColumnWrapper {
        QuoteFormContent(
            modifier = Modifier.padding(32.dp),
            quoteForm = QuoteForm(quote = "", thought = "", bookId = 1L, page = 1),
            bookCandidates = emptyList(),
            onUpdateQuote = {},
            onUpdateBookFilter = {},
            onSelectBook = {}
        )
    }
}
