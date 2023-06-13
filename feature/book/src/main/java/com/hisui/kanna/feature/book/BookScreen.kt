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

package com.hisui.kanna.feature.book

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hisui.kanna.core.designsystem.component.FloatingEditButton
import com.hisui.kanna.core.designsystem.component.KannaDivider
import com.hisui.kanna.core.designsystem.theme.KannaTheme
import com.hisui.kanna.core.model.Book
import com.hisui.kanna.core.model.Quote
import com.hisui.kanna.core.model.bookForPreview
import com.hisui.kanna.core.model.quoteForPreview
import com.hisui.kanna.core.ui.preview.PreviewColumnWrapper
import com.hisui.kanna.core.ui.util.format
import com.hisui.kanna.feature.book.component.RatingStars
import kotlinx.datetime.Instant

@Composable
internal fun BookRoute(
    viewModel: BookViewModel = hiltViewModel(),
    onOpenEdit: (id: Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    BookScreen(
        uiState = uiState,
        onOpenEdit = onOpenEdit
    )
}

@Composable
internal fun BookScreen(
    uiState: BookUiState,
    onOpenEdit: (id: Long) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        when (uiState) {
            is BookUiState.Success ->
                BookContent(
                    book = uiState.book,
                    onOpenEdit = { onOpenEdit(uiState.book.id) }
                )

            is BookUiState.Loading -> {}
            is BookUiState.Error -> {}
        }
    }
}

@Composable
private fun BookContent(
    book: Book,
    onOpenEdit: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)
    ) {
        val scrollState = rememberLazyListState()
        LazyColumn(
            state = scrollState,
            verticalArrangement = spacedBy(8.dp)
        ) {
            item { Spacer(modifier = Modifier.height(32.dp)) }
            item { Title(title = book.title) }
            item { PropertiesSection(authorName = book.author.name, genre = book.genre, readDate = book.readDate) }
            item { EvaluationSection(thought = book.thought, rating = book.rating) }
            item { MemoSection(memo = book.memo) }
            quotesSection(quotes = book.quotes)
        }

        Spacer(modifier = Modifier.height(96.dp))

        FloatingEditButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 16.dp),
            visible = !scrollState.canScrollForward || !scrollState.canScrollBackward,
            onClick = onOpenEdit
        )
    }
}

@Composable
private fun BookContentPreviewBase(noThought: Boolean = false) {
    KannaTheme {
        PreviewColumnWrapper {
            BookContent(
                book = bookForPreview(
                    thought = if (noThought) "" else "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
                    memo = "Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam.",
                    quotes = listOf(
                        quoteForPreview(page = 10),
                        quoteForPreview(page = 20),
                        quoteForPreview(page = 30)
                    )
                ),
                onOpenEdit = {}
            )
        }
    }
}

@Preview(device = Devices.PIXEL_4_XL)
@Composable
private fun BookContentPhonePreview() { BookContentPreviewBase() }

@Preview(device = Devices.PIXEL_4_XL)
@Composable
private fun BookContentPhoneNoThoughtPreview() { BookContentPreviewBase(noThought = true) }

@Preview(device = Devices.TABLET)
@Composable
private fun BookContentTablePreview() { BookContentPreviewBase() }

@Preview(device = Devices.DESKTOP)
@Composable
private fun BookContentDesktopPreview() { BookContentPreviewBase() }

@Composable
private fun Title(title: String) {
    Text(text = title, style = MaterialTheme.typography.headlineMedium)
}

@Composable
private fun PropertiesSection(
    authorName: String,
    genre: String,
    readDate: Instant
) {
    Text(text = authorName, style = MaterialTheme.typography.titleMedium)

    Row(
        modifier = Modifier.padding(top = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = spacedBy(8.dp)
    ) {
        Text(
            modifier = Modifier
                .clip(MaterialTheme.shapes.extraSmall)
                .background(MaterialTheme.colorScheme.tertiary)
                .padding(horizontal = 6.dp, vertical = 2.dp),
            text = genre,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onTertiary
        )
        Text(
            text = readDate.format(),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun EvaluationSection(thought: String, rating: Int) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        KannaDivider()

        Box(modifier = Modifier.offset(x = (-4).dp)) {
            RatingStars(
                stars = rating,
                onUpdate = { /* do nothing */ },
                spacedBy = 2.dp,
                size = 24.dp
            )
        }

        if (thought.isBlank()) {
            Text(
                text = stringResource(id = R.string.no_thought),
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            Text(text = thought)
        }
    }
}

@Composable
private fun MemoSection(memo: String) {
    if (memo.isBlank()) {
        return
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        KannaDivider()
        Text(text = stringResource(id = R.string.memo), fontWeight = FontWeight.SemiBold)
        Text(text = memo)
    }
}

private fun LazyListScope.quotesSection(quotes: List<Quote>) {
    if (quotes.isEmpty()) {
        return
    }

    item { KannaDivider() }
    item {
        Text(
            text = stringResource(id = R.string.quotes_from_this_book),
            style = MaterialTheme.typography.titleMedium
        )
    }

    items(quotes) { quote ->
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = quote.quote,
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = stringResource(id = R.string.page_of, quote.page),
                style = MaterialTheme.typography.labelMedium
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}
