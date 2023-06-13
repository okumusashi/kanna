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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hisui.kanna.core.designsystem.component.FloatingEditButton
import com.hisui.kanna.core.designsystem.component.KannaDivider
import com.hisui.kanna.core.model.Quote

@Composable
internal fun QuoteRoute(
    viewMode: QuoteViewModel = hiltViewModel(),
    onOpenEdit: (id: Long) -> Unit
) {
    val uiState by viewMode.uiState.collectAsState()
    QuoteScreen(
        uiState = uiState,
        onOpenEdit = onOpenEdit
    )
}

@Composable
internal fun QuoteScreen(
    uiState: QuoteUiState,
    onOpenEdit: (id: Long) -> Unit
) {
    when (uiState) {
        is QuoteUiState.ShowQuote -> QuoteContent(
            quote = uiState.quote,
            onOpenEdit = { onOpenEdit(uiState.quote.id) }
        )
        is QuoteUiState.Error -> { /* TODO */ }
        is QuoteUiState.Loading -> { /* TODO */ }
    }
}

@Composable
private fun QuoteContent(
    quote: Quote,
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
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item { Spacer(modifier = Modifier.height(32.dp)) }
            item { Quote(title = quote.quote) }

            item { Spacer(modifier = Modifier.height(8.dp)) }
            item { BookInfoSection(title = quote.bookTitle, author = quote.author, page = quote.page) }

            item { KannaDivider() }
            item { ThoughtSection(thought = quote.thought) }
        }

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
private fun Quote(title: String) {
    Text(text = "\"$title\"", style = MaterialTheme.typography.titleMedium)
}

@Composable
private fun BookInfoSection(
    title: String,
    author: String,
    page: Int
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.W600)
        )
        Text(
            text = author,
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = stringResource(id = R.string.page_of, page),
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
private fun ThoughtSection(thought: String) {
    if (thought.isBlank()) {
        Text(
            text = stringResource(
                id = com.hisui.kanna.core.ui.R.string.no_thought,
                stringResource(id = com.hisui.kanna.core.ui.R.string.quote)
            ),
            fontStyle = FontStyle.Italic,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    } else {
        Text(text = thought)
    }
}
