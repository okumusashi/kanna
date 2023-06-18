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

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.hisui.kanna.core.model.BookForQuote

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BookSelection(
    modifier: Modifier = Modifier,
    viewModel: BookSelectionViewModel = hiltViewModel(),
    initial: String = "",
    onSelect: (BookForQuote) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(initial) {
        viewModel.selectBook(title = initial, selected = initial.isNotBlank())
    }

    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(viewModel.event) {
        viewModel.event.collect { event ->
            when (event) {
                BookSelectionEvent.ShouldFocus -> focusRequester.requestFocus()
            }
        }
    }

    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = uiState.expanded,
        onExpandedChange = { viewModel.changeDropdownExpanded() }
    ) {
        OutlinedTextField(
            modifier = Modifier
                .menuAnchor()
                .focusRequester(focusRequester),
            value = uiState.title,
            readOnly = uiState.bookSelected,
            onValueChange = viewModel::filterBooks,
            trailingIcon = {
                if (uiState.bookSelected) {
                    Icon(
                        modifier = Modifier.clickable { viewModel.resetBook() },
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Clear"
                    )
                }
            },
            label = { Text(stringResource(id = com.hisui.kanna.core.ui.R.string.book_title)) }
        )

        ExposedDropdownMenu(
            expanded = uiState.expanded,
            onDismissRequest = viewModel::shrinkDropdown
        ) {
            uiState.bookCandidates
                .ifEmpty {
                    if (uiState.bookSelected) {
                        return@ExposedDropdownMenu
                    }
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
                            focusRequester.freeFocus()
                            viewModel.selectBook(item.title)
                        }
                    )
                }
        }
    }
}
