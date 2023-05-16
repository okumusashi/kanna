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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.hisui.kanna.core.designsystem.component.FormDialog
import com.hisui.kanna.core.designsystem.theme.KannaTheme

@Composable
internal fun GenreSelection(
    modifier: Modifier = Modifier,
    viewModel: GenreSelectionViewModel = hiltViewModel(),
    selected: String?,
    onSelect: (String) -> Unit,
) {
    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is GenreSelectionEvent.Select -> onSelect(event.genre)
            }
        }
    }

    val state by viewModel.state.collectAsState()

    when (val uiState = state) {
        GenreSelectionUiState.Loading -> {}
        is GenreSelectionUiState.NoGenre -> {}
        is GenreSelectionUiState.ShowList ->
            GenreSelection(
                modifier = modifier,
                list = uiState.genres,
                selected = selected,
                showCreateDialog = uiState.showCreateDialog,
                onSelect = onSelect,
                onShowCreateDialog = viewModel::showCreateDialog,
                onDismissCreateDialog = viewModel::dismissCreateDialog,
                onCreate = viewModel::create,
            )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun GenreSelection(
    modifier: Modifier = Modifier,
    list: List<String>,
    selected: String?,
    showCreateDialog: Boolean,
    onSelect: (String) -> Unit,
    onShowCreateDialog: () -> Unit,
    onDismissCreateDialog: () -> Unit,
    onCreate: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            modifier = Modifier.menuAnchor(),
            value = selected ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(id = com.hisui.kanna.core.ui.R.string.genre)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = {
                    Text(
                        text = stringResource(com.hisui.kanna.core.ui.R.string.add_new),
                        color = MaterialTheme.colorScheme.tertiary
                    )
                },
                trailingIcon = {
                    Icon(imageVector = Icons.Filled.PersonAdd, contentDescription = "Add")
                },
                onClick = onShowCreateDialog
            )

            list.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item) },
                    onClick = {
                        onSelect(item)
                        expanded = false
                    }
                )
            }
        }
    }

    if (showCreateDialog) {
        CreateGenreDialog(
            onDismiss = onDismissCreateDialog,
            onCreate = onCreate,
        )
    }
}

@Composable
private fun CreateGenreDialog(
    onDismiss: () -> Unit,
    onCreate: (name: String) -> Unit
) {
    var name by remember { mutableStateOf("") }

    FormDialog(
        title = stringResource(R.string.add_genre),
        onDismiss = onDismiss,
        onCreate = { onCreate(name) }
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(stringResource(com.hisui.kanna.core.ui.R.string.name)) }
        )
    }
}

@Preview
@Composable
private fun CreateGenreScreenPreview() {
    KannaTheme {
        CreateGenreDialog(
            onDismiss = {},
            onCreate = {}
        )
    }
}