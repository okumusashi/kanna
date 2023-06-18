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

package com.hisui.kanna.core.model

import kotlinx.datetime.Instant

data class Quote(
    val id: Long,
    val page: Int,
    val quote: String,
    val thought: String,
    val createdAt: Instant,
    val bookId: Long,
    val bookTitle: String,
    val authorId: String,
    val author: String
)

data class QuoteForm(
    val quote: String,
    val bookId: Long,
    val page: Int?,
    val thought: String
)

enum class QuoteField { QUOTE, BOOK, PAGE, THOUGHT }

fun quoteForPreview(
    page: Int = 123,
    quote: String = "The world is a book, and those who do not travel read only a page."
): Quote =
    Quote(
        id = 1L,
        page = page,
        quote = quote,
        thought = "I love this quote!",
        createdAt = Instant.parse("2022-01-01T00:00:00Z"),
        bookId = 1L,
        bookTitle = "The World",
        authorId = "John Doe",
        author = "John Doe"
    )
