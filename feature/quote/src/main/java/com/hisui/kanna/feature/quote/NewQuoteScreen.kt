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

package com.hisui.kanna.feature.quote

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hisui.kanna.core.model.BookForQuote
import com.hisui.kanna.core.model.NewQuote
import com.hisui.kanna.core.ui.component.KannaTopBar

@Composable
internal fun NewQuoteRoute(
    viewModel: NewQuoteViewModel = hiltViewModel(),
    isWidthCompact: Boolean,
    isHeightCompact: Boolean,
    popBackStack: () -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                NewQuoteEvent.Created -> popBackStack()
            }
        }
    }

    val uiState by viewModel.uiState.collectAsState()

    NewQuoteScreen(
        isCompact = isHeightCompact || isWidthCompact,
        uiState = uiState,
        onUpdateQuote = viewModel::updateQuote,
        onSelectBook = viewModel::selectBook,
        onUpdateBookFilter = viewModel::filterBooks,
        onCreate = viewModel::create,
        onExit = popBackStack
    )
}

@Composable
internal fun NewQuoteScreen(
    isCompact: Boolean,
    uiState: NewQuoteUiState,
    onUpdateQuote: (NewQuote) -> Unit,
    onSelectBook: (BookForQuote) -> Unit,
    onUpdateBookFilter: (q: String) -> Unit,
    onCreate: (NewQuote) -> Unit,
    onExit: () -> Unit
) {
    Scaffold(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(if (isCompact) 1f else 0.65f),
        topBar = {
            KannaTopBar(
                title = stringResource(id = R.string.add_quote),
                submitButtonTitle = stringResource(id = com.hisui.kanna.core.ui.R.string.create),
                onClickNavigationIcon = onExit,
                onSubmit = {
                    if (uiState is NewQuoteUiState.AddQuote) {
                        onCreate(uiState.newQuote)
                    }
                },
                enabled = uiState is NewQuoteUiState.AddQuote
            )
        }
    ) { paddingValues ->
        when (uiState) {
            NewQuoteUiState.Loading -> {}
            is NewQuoteUiState.AddQuote -> {
                AddQuoteScreen(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(paddingValues)
                        .padding(16.dp),
                    newQuote = uiState.newQuote,
                    bookCandidates = uiState.bookCandidates,
                    onUpdateQuote = onUpdateQuote,
                    onUpdateBookFilter = onUpdateBookFilter,
                    onSelectBook = onSelectBook
                )
            }
        }
    }
}

@Composable
private fun AddQuoteScreen(
    modifier: Modifier = Modifier,
    newQuote: NewQuote,
    bookCandidates: List<BookForQuote>,
    onUpdateQuote: (NewQuote) -> Unit,
    onUpdateBookFilter: (q: String) -> Unit,
    onSelectBook: (BookForQuote) -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            value = newQuote.quote,
            onValueChange = { onUpdateQuote(newQuote.copy(quote = it)) },
            label = { Text(text = stringResource(id = R.string.quote)) },
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
        )

        BookSelection(
            filteredBooks = bookCandidates,
            onUpdateFilter = onUpdateBookFilter,
            onSelect = { book ->
                onSelectBook(book)
                onUpdateQuote(newQuote.copy(bookId = book.id))
            }
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = newQuote.page?.toString() ?: "",
            onValueChange = { onUpdateQuote(newQuote.copy(page = it.toIntOrNull())) },
            label = { Text(text = stringResource(id = R.string.page)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            value = newQuote.thought,
            onValueChange = { onUpdateQuote(newQuote.copy(thought = it)) },
            label = { Text(text = stringResource(id = com.hisui.kanna.core.ui.R.string.thought)) },
            keyboardOptions = KeyboardOptions(KeyboardCapitalization.Sentences)
        )
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
