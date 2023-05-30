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

package com.hisui.kanna.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hisui.kanna.core.designsystem.theme.KannaTheme
import com.hisui.kanna.core.model.Author
import com.hisui.kanna.core.model.Book
import com.hisui.kanna.core.model.ReadStatus
import com.hisui.kanna.core.ui.util.dateTimeFormatter
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant

@Composable
internal fun HomeRoute(
    viewModel: HomeViewModel = hiltViewModel(),
    onNewBookFabClick: () -> Unit
) {
    val homeUiState by viewModel.uiState.collectAsState()

    HomeScreen(
        homeUiState = homeUiState,
        onNewBookFabClick = onNewBookFabClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeScreen(
    homeUiState: HomeUiState,
    onNewBookFabClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        val lazyGridState = rememberLazyGridState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .padding(bottom = 16.dp),
                text = stringResource(R.string.home_title),
                style = MaterialTheme.typography.headlineMedium
            )

            LazyVerticalGrid(
                modifier = Modifier.fillMaxWidth(),
                state = lazyGridState,
                columns = GridCells.Adaptive(300.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // TODO: Filter dropdown

                when (homeUiState) {
                    is HomeUiState.Loading -> {
                        item {
                            CircularProgressIndicator(
                                modifier = Modifier.testTag(stringResource(id = R.string.loading_tag))
                            )
                        }
                    }
                    is HomeUiState.Empty -> {
                        // TODO: Add explanation and button (if not using fab) to add a new book
                    }
                    is HomeUiState.RecentBooks -> {
                        bookList(books = homeUiState.books)
                    }
                }
            }
        }

        ExtendedFloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            onClick = onNewBookFabClick,
            expanded = !lazyGridState.canScrollBackward || !lazyGridState.canScrollForward,
            icon = { Icon(Icons.Filled.Add, "Add new book") },
            text = { Text(text = stringResource(R.string.add_book)) }
        )
    }
}

@Composable
private fun HomeScreenPreview() {
    KannaTheme {
        val state = HomeUiState.RecentBooks(
            books = listOf(
                Book(
                    id = 1,
                    title = "The Great Gatsby",
                    author = Author(
                        id = "",
                        name = "F. Scott Fitzgerald",
                        memo = "",
                        isFavourite = false
                    ),
                    readDate = Instant.parse("2023-03-01T00:00:00Z"),
                    thought = "",
                    memo = "",
                    rating = 5,
                    genre = "",
                    status = ReadStatus.HAVE_READ
                ),
                Book(
                    id = 2,
                    title = "Nineteen Eighty-Four",
                    author = Author(
                        id = "",
                        name = "George Orwell",
                        memo = "",
                        isFavourite = false
                    ),
                    readDate = Instant.parse("2023-02-01T00:00:00Z"),
                    thought = "",
                    memo = "",
                    rating = 5,
                    genre = "",
                    status = ReadStatus.HAVE_READ
                ),
                Book(
                    id = 3,
                    title = "Silent Spring",
                    author = Author(
                        id = "",
                        name = "Rachel Carson",
                        memo = "",
                        isFavourite = false
                    ),
                    readDate = Instant.parse("2023-01-01T00:00:00Z"),
                    thought = "",
                    memo = "",
                    rating = 5,
                    genre = "",
                    status = ReadStatus.HAVE_READ
                )
            )
        )
        HomeScreen(homeUiState = state, onNewBookFabClick = {})
    }
}

@Preview @Composable
private fun HomeScreenPreviewCompact() { HomeScreenPreview() }

@Preview(device = Devices.TABLET)
@Composable
private fun HomeScreenPreviewMedium() { HomeScreenPreview() }

@Preview(device = Devices.DESKTOP)
@Composable
private fun HomeScreenPreviewLarge() { HomeScreenPreview() }

private fun LazyGridScope.bookList(books: List<Book>) {
    items(books) { book ->
        Card {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = book.author.name,
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    text = dateTimeFormatter().format(book.readDate.toJavaInstant()),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}
