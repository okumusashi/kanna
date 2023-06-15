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

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.hisui.kanna.core.model.BookForQuote

@Composable
internal fun BookSelection(
    initial: String? = null,
    viewModel: BookSelectionViewModel = hiltViewModel(),
    onSelect: (BookForQuote) -> Unit
) {
    val filteredBooksState by viewModel.bookCandidates.collectAsState()
    BookSelection(
        filteredBooks = filteredBooksState,
        onUpdateFilter = viewModel::filterBooks,
        onSelect = onSelect,
        initial = initial ?: ""
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BookSelection(
    modifier: Modifier = Modifier,
    initial: String,
    filteredBooks: List<BookForQuote>,
    onUpdateFilter: (q: String) -> Unit,
    onSelect: (BookForQuote) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var title by remember(initial) { mutableStateOf(initial) }

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
