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

data class Book(
    val id: Long,
    val title: String,
    val readDate: Instant,
    val memo: String,
    val thought: String,
    val rating: Int,
    val author: Author,
    val genre: String,
    val status: BookReadStatus,
    val quotes: List<Quote>
)

enum class BookSorter {
    TITLE, READ_DATE
}

/**
 * This is used for NewQuote to select the book.
 *
 * @param id is [Book.id]
 * @param title is [Book.title] with [Author.name]
 */
data class BookForQuote(val id: Long, val title: String)

data class NewBook(
    val title: String,
    val readDate: Instant,
    val memo: String?,
    val thought: String,
    val rating: Int,
    val authorId: String,
    val genreId: String,
    val statusId: Long
)

fun bookForPreview(
    id: Long = 1,
    title: String = "Nineteen Eighty-Four",
    authorName: String = "George Orwell",
    thought: String = "",
    memo: String = "",
    genre: String = "Novel",
    rating: Int = 5,
    status: BookStatus = BookStatus.HAVE_READ,
    quotes: List<Quote> = emptyList()
): Book =
    Book(
        id = id,
        title = title,
        author = Author(
            id = "",
            name = authorName,
            memo = "",
            isFavourite = false
        ),
        readDate = Instant.parse("2023-03-01T00:00:00Z"),
        thought = thought,
        memo = memo,
        rating = rating,
        genre = genre,
        status = BookReadStatus(id = BookStatus.values().indexOf(status).toLong() + 1, status = status),
        quotes = quotes
    )
