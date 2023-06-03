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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import com.hisui.kanna.core.designsystem.theme.KannaTheme
import com.hisui.kanna.core.model.Book
import com.hisui.kanna.core.model.bookForPreview
import com.hisui.kanna.core.ui.preview.PreviewColumnWrapper
import com.hisui.kanna.core.ui.util.format
import kotlinx.datetime.Instant

@Composable
internal fun BookRoute(
    viewModel: BookViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    BookScreen(uiState = uiState)
}

@Composable
internal fun BookScreen(
    uiState: BookUiState
) {
    Column(modifier = Modifier.fillMaxSize()) {
        when (uiState) {
            is BookUiState.Success ->
                BookContent(uiState.book)

            is BookUiState.Loading -> {}
            is BookUiState.Error -> {}
        }
    }
}

@Composable
private fun BookContent(
    book: Book
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        Column(verticalArrangement = spacedBy(8.dp)) {
            Title(title = book.title)
            PropertiesSection(authorName = book.author.name, genre = book.genre, readDate = book.readDate)
            EvaluationSection(thought = book.thought, rating = book.rating)
            MemoSection(memo = book.memo)
        }

        FloatingActionButton(
            modifier = Modifier.align(Alignment.BottomEnd),
            onClick = { /*TODO*/ }
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit book"
            )
        }
    }
}

@Composable
private fun BookContentPreviewBase(noThought: Boolean = false) {
    KannaTheme {
        PreviewColumnWrapper {
            BookContent(
                book = bookForPreview(
                    thought = if (noThought) "" else "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
                    memo = "Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam."
                )
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
private fun BookContentDivider() {
    Divider(
        modifier = Modifier.padding(vertical = 8.dp),
        thickness = 0.5f.dp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

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
        BookContentDivider()

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
        BookContentDivider()
        Text(text = stringResource(id = R.string.memo), fontWeight = FontWeight.SemiBold)
        Text(text = memo)
    }
}
