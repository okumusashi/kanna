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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Divider
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.hisui.kanna.core.designsystem.component.AddButton
import com.hisui.kanna.core.designsystem.theme.KannaTheme
import com.hisui.kanna.core.model.Quote
import com.hisui.kanna.core.ui.preview.PreviewColumnWrapper

@Composable
internal fun QuoteScreen(
    viewModel: QuoteViewModel = hiltViewModel(),
    onOpenNewQuoteScreen: () -> Unit,
    onOpenNewBookScreen: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    QuoteScreen(
        uiState = uiState,
        onOpenNewQuoteScreen = onOpenNewQuoteScreen,
        onOpenNewBookScreen = onOpenNewBookScreen
    )
}

@Composable
private fun QuoteScreen(
    uiState: QuoteUiState,
    onOpenNewQuoteScreen: () -> Unit,
    onOpenNewBookScreen: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .padding(bottom = 16.dp),
            text = stringResource(R.string.quotes),
            style = MaterialTheme.typography.headlineMedium
        )

        Box {
            when (uiState) {
                QuoteUiState.Loading -> {
                    // TODO: Add indicator
                }

                QuoteUiState.NoBook ->
                    NoBookScreen(onOpenNewBookScreen = onOpenNewBookScreen)

                QuoteUiState.NoQuote ->
                    NoQuoteScreen(onOpenNewQuoteScreen = onOpenNewQuoteScreen)

                is QuoteUiState.ShowQuotes ->
                    ShowQuotesScreen(
                        quotes = uiState.quotes,
                        onOpenNewQuoteScreen = onOpenNewQuoteScreen
                    )
            }
        }
    }
}

@Composable
private fun QuoteScreenPreview() {
    val quotes = (1..10)
        .map { previewQuote() }
        .distinctBy { it.quote }
        .sortedByDescending { it.createdAt }

    PreviewColumnWrapper {
        QuoteScreen(
            uiState = QuoteUiState.ShowQuotes(quotes = quotes),
            onOpenNewQuoteScreen = {},
            onOpenNewBookScreen = {},
        )
    }
}

@Preview @Composable
private fun QuoteScreenCompactPreview() { QuoteScreenPreview() }

@Preview(device = Devices.PIXEL_C) @Composable
private fun QuoteScreenCompactMedium() { QuoteScreenPreview() }

@Preview(device = Devices.DESKTOP) @Composable
private fun QuoteScreenCompactLarge() { QuoteScreenPreview() }

@Preview
@Composable
private fun NoBookScreenPreview() {
    PreviewColumnWrapper {
        QuoteScreen(
            uiState = QuoteUiState.NoBook,
            onOpenNewQuoteScreen = {},
            onOpenNewBookScreen = {},
        )
    }
}

@Composable
private fun NoBookScreen(onOpenNewBookScreen: () -> Unit) {
    OutlinedCard(
        modifier = Modifier.padding(16.dp),
        shape = MaterialTheme.shapes.large,
    ) {
        Text(
            modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
            text = stringResource(id = R.string.no_book_yet),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
        )

        Text(
            modifier = Modifier.padding(16.dp),
            text = stringResource(id = R.string.lets_add_book),
            style = MaterialTheme.typography.bodyMedium
        )

        AddButton(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 16.dp),
            onClick = onOpenNewBookScreen,
            buttonText = stringResource(com.hisui.kanna.core.ui.R.string.add_books)
        )
    }
}

@Composable
private fun NoQuoteScreen(
    modifier: Modifier = Modifier,
    onOpenNewQuoteScreen: () -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize(),
            columns = GridCells.Adaptive(300.dp),
            horizontalArrangement = Arrangement.spacedBy(
                16.dp,
                alignment = Alignment.CenterHorizontally
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.book))
                val progress by animateLottieCompositionAsState(
                    composition = composition,
                    iterations = 5,
                    speed = 0.5f,
                )

                LottieAnimation(
                    modifier = Modifier.sizeIn(maxWidth = 240.dp, maxHeight = 240.dp),
                    composition = composition,
                    progress = { progress }
                )
            }

            item {
                Box(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxSize()
                ) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = stringResource(id = R.string.no_quote_yet),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

        AddQuoteFab(
            modifier = Modifier.align(Alignment.BottomEnd),
            onClick = onOpenNewQuoteScreen,
            expanded = true,
        )
    }
}

@Preview
@Composable
private fun NoQuoteScreenPreview() {
    PreviewColumnWrapper {
        QuoteScreen(
            uiState = QuoteUiState.NoQuote,
            onOpenNewQuoteScreen = {},
            onOpenNewBookScreen = {},
        )
    }
}

@Composable
private fun ShowQuotesScreen(
    modifier: Modifier = Modifier,
    quotes: List<Quote>,
    onOpenNewQuoteScreen: () -> Unit
) {
    val lazyListState = rememberLazyListState()

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = lazyListState,
        ) {
            items(quotes) { quote ->
                QuoteItem(quote = quote)
            }
        }

        AddQuoteFab(
            modifier = Modifier.align(Alignment.BottomEnd),
            onClick = onOpenNewQuoteScreen,
            expanded = !lazyListState.canScrollBackward || !lazyListState.canScrollForward,
        )
    }
}

@Composable
private fun AddQuoteFab(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    expanded: Boolean
) {
    ExtendedFloatingActionButton(
        modifier = modifier,
        onClick = onClick,
        expanded = expanded,
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        icon = { Icon(Icons.Filled.Add, "Add new quote") },
        text = { Text(text = stringResource(R.string.add_quote)) }
    )
}

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

            Text(
                modifier = Modifier.clickable { /* TODO: Navigate to author screen */ },
                text = quote.author,
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.labelMedium,
            )
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
