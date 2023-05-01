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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hisui.kanna.core.designsystem.theme.KannaTheme
import com.hisui.kanna.core.model.Book
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
internal fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val homeUiState by viewModel.uiState.collectAsState()

    HomeScreen(homeUiState = homeUiState)
}

@Composable
private fun HomeScreen(homeUiState: HomeUiState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            text = stringResource(R.string.title),
            style = MaterialTheme.typography.headlineMedium
        )

        LazyVerticalGrid(
            modifier = Modifier.fillMaxWidth(),
            columns = GridCells.Adaptive(300.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // TODO: Filter dropdown

            when (homeUiState) {
                is HomeUiState.Loading -> {
                    // TODO: Add indicator
                }
                is HomeUiState.Empty -> {
                    // TODO: Add explanation and button (if not using fab) to add a new book
                }
                is HomeUiState.RecentBooks -> {
                    bookList(books = homeUiState.books)
                }
                else -> {}
            }
        }
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
                    author = "F. Scott Fitzgerald",
                    readDate = Instant.parse("2023-03-01T00:00:00Z"),
                    memo = "",
                    rating = 5,
                    authorId = "",
                    authorMemo = "",
                    genre = "",
                ),
                Book(
                    id = 2,
                    title = "Nineteen Eighty-Four",
                    author = "George Orwell",
                    readDate = Instant.parse("2023-02-01T00:00:00Z"),
                    memo = "",
                    rating = 5,
                    authorId = "",
                    authorMemo = "",
                    genre = "",
                ),
                Book(
                    id = 3,
                    title = "Silent Spring",
                    author = "Rachel Carson",
                    readDate = Instant.parse("2023-01-01T00:00:00Z"),
                    memo = "",
                    rating = 5,
                    authorId = "",
                    authorMemo = "",
                    genre = "",
                ),
            )
        )
        HomeScreen(homeUiState = state)
    }
}

@Preview @Composable
private fun HomeScreenPreviewCompact() { HomeScreenPreview() }

@Preview(device = Devices.TABLET) @Composable
private fun HomeScreenPreviewMedium() { HomeScreenPreview() }

@Preview(device = Devices.DESKTOP) @Composable
private fun HomeScreenPreviewLarge() { HomeScreenPreview() }

private fun LazyGridScope.bookList(books: List<Book>) {
    items(books) { book ->
        Card {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = book.author,
                    style = MaterialTheme.typography.labelMedium,
                )
                val dateTimeFormatter = DateTimeFormatter.ofPattern("MMM d yyyy").withZone(ZoneId.systemDefault())
                Text(
                    text = dateTimeFormatter.format(book.readDate.toJavaInstant()),
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
    }
}
