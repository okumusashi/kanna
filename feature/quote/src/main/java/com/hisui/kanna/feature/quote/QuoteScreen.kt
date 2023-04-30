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

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hisui.kanna.core.designsystem.theme.KannaTheme
import com.hisui.kanna.core.model.Quote

@Composable
internal fun QuoteScreen(
    viewModel: QuoteViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    QuoteScreen(uiState = uiState)
}

@Composable
private fun QuoteScreen(uiState: QuoteUiState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        when (uiState) {
            QuoteUiState.Loading -> {
                // TODO: Add indicator
            }
            QuoteUiState.Empty -> {
                // TODO: Add explanation and add button
            }
            is QuoteUiState.Success -> {
                LazyColumn {
                    items(uiState.quotes) { quote ->
                        QuoteItem(quote = quote)
                    }
                }
            }
        }
    }
}

@Composable
private fun QuoteScreenPreview() {
    KannaTheme {
        val quotes = (1..10)
            .map { previewQuote() }
            .distinctBy { it.quote }
            .sortedByDescending { it.createdAt }

        Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
            QuoteScreen(uiState = QuoteUiState.Success(quotes = quotes))
        }
    }
}

@Preview @Composable
private fun QuoteScreenCompactPreview() { QuoteScreenPreview() }

@Preview(device = Devices.PIXEL_C) @Composable
private fun QuoteScreenCompactMedium() { QuoteScreenPreview() }

@Preview(device = Devices.DESKTOP) @Composable
private fun QuoteScreenCompactLarge() { QuoteScreenPreview() }

@Composable
private fun QuoteItem(quote: Quote) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = quote.quote,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleMedium,
                fontStyle = FontStyle.Italic
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                modifier = Modifier
                    .clickable { /* TODO: Navigate to author screen */ },
                text = quote.author,
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.labelMedium,
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    modifier = Modifier.clickable { /* TODO: Navigate to book screen */ },
                    text = "${quote.bookTitle}, ${quote.page}",
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }

        Divider(
            modifier = Modifier.padding(vertical = 16.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Preview
@Composable
private fun QuoteItemPreview() {
    KannaTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            QuoteItem(quote = previewQuote())
        }
    }
}
