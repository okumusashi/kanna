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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.hisui.kanna.core.designsystem.theme.KannaTheme
import com.hisui.kanna.core.model.Author
import com.hisui.kanna.core.ui.R

@Composable
internal fun AuthorSelection(
    modifier: Modifier = Modifier,
    viewModel: AuthorSelectionViewModel = hiltViewModel(),
    selected: Author?,
    onSelect: (Author) -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is AuthorSelectionEvent.Select -> onSelect(event.author)
            }
        }
    }

    val state by viewModel.state.collectAsState()

    when (val uiState = state) {
        AuthorSelectionUiState.Loading -> {}
        is AuthorSelectionUiState.NoAuthor -> {}
        is AuthorSelectionUiState.ShowList -> {
            AuthorSelection(
                modifier = modifier,
                list = uiState.authors,
                selected = selected,
                onSelect = onSelect,
                showCreateDialog = uiState.showCreateDialog,
                onShowCreateDialog = viewModel::showCreateDialog,
                onDismissCreateDialog = viewModel::dismissCreateDialog,
                onCreate = viewModel::createAuthor,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AuthorSelection(
    modifier: Modifier = Modifier,
    list: List<Author>,
    selected: Author?,
    onSelect: (Author) -> Unit,
    showCreateDialog: Boolean,
    onShowCreateDialog: () -> Unit,
    onDismissCreateDialog: () -> Unit,
    onCreate: (name: String, memo: String?) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            modifier = Modifier.menuAnchor(),
            value = selected?.name ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(id = R.string.author)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Add New") },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Filled.PersonAdd,
                        contentDescription = "Calendar"
                    )
                },
                onClick = onShowCreateDialog
            )
            list.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item.name) },
                    onClick = {
                        onSelect(item)
                        expanded = false
                    }
                )
            }
        }
    }

    if (showCreateDialog) {
        Dialog(onDismissRequest = onDismissCreateDialog) {
            CreateAuthorScreen(
                onDismiss = onDismissCreateDialog,
                onCreate = onCreate
            )
        }
    }
}

private val authorPreviewList: List<Author> =
    listOf(
        Author(id = "1", name = "Ernest Hemingway", memo = "", isFavourite = false),
        Author(id = "2", name = "Rachel Carson", memo = "", isFavourite = false),
        Author(id = "3", name = "芥川龍之介", memo = "", isFavourite = false),
    )

@Preview
@Composable
private fun AuthorSelectionPreview() {
    KannaTheme {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            AuthorSelection(
                modifier = Modifier.align(Alignment.Center),
                list = authorPreviewList,
                selected = null,
                onSelect = {},
                showCreateDialog = false,
                onShowCreateDialog = {},
                onDismissCreateDialog = {},
                onCreate = { _, _ -> }
            )
        }
    }
}

@Composable
private fun CreateAuthorScreen(
    onDismiss: () -> Unit,
    onCreate: (name: String, memo: String?) -> Unit
) {
    Column(
        modifier = Modifier
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(32.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Add an author",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        var name by remember { mutableStateOf("") }

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") }
        )

        var memo: String? by remember { mutableStateOf(null) }

        OutlinedTextField(
            value = memo ?: "",
            onValueChange = { memo = it },
            label = { Text("Memo (Optional)") },
            placeholder = { Text("For your memo.") }
        )

        Row(
            modifier = Modifier.align(Alignment.End),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel")
            }
            TextButton(
                onClick = {
                    onCreate(name, memo)
                    onDismiss()
                }
            ) {
                Text("Create")
            }
        }
    }
}

@Preview
@Composable
private fun CreateAuthorScreenPreview() {
    KannaTheme {
        CreateAuthorScreen(
            onDismiss = {},
            onCreate = { _, _ -> }
        )
    }
}
