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

package com.hisui.kanna.core.data.mapper

import com.hisui.kanna.core.database.entity.BookAndAuthorEntity
import com.hisui.kanna.core.database.entity.BookAndAuthorEntityWithQuotes
import com.hisui.kanna.core.database.entity.BookEntity
import com.hisui.kanna.core.database.entity.BookForQuoteEntity
import com.hisui.kanna.core.database.entity.BookReadStatusEntity
import com.hisui.kanna.core.model.Book
import com.hisui.kanna.core.model.BookForQuote
import com.hisui.kanna.core.model.BookForm
import com.hisui.kanna.core.model.BookReadStatus
import com.hisui.kanna.core.model.BookStatus
import com.hisui.kanna.core.model.Quote

internal fun BookForm.asEntity(): BookEntity =
    BookEntity(
        title = title,
        readDate = readDate,
        memo = memo,
        thought = thought,
        rating = rating,
        authorId = authorId,
        genreId = genreId,
        statusId = statusId
    )

internal fun BookForm.asEntity(id: Long): BookEntity =
    BookEntity(
        id = id,
        title = title,
        readDate = readDate,
        memo = memo,
        thought = thought,
        rating = rating,
        authorId = authorId,
        genreId = genreId,
        statusId = statusId
    )

internal fun List<BookAndAuthorEntity>.asExternalModel(): List<Book> =
    this.map { it.asExternalModel(quotes = emptyList()) }

internal fun BookAndAuthorEntityWithQuotes.asExternalModel(): Book =
    bookAndAuthor.asExternalModel(
        quotes = quotes.map { it.asExternalModel(bookAndAuthor = bookAndAuthor) }
    )

internal fun BookAndAuthorEntity.asExternalModel(quotes: List<Quote>): Book =
    Book(
        id = book.id,
        title = book.title,
        author = author.asExternalModel(),
        genre = book.genreId ?: "",
        readDate = book.readDate,
        memo = book.memo ?: "",
        thought = book.thought,
        rating = book.rating,
        status = status.asExternalModel(),
        quotes = quotes
    )

private fun BookReadStatusEntity.asExternalModel(): BookReadStatus =
    BookReadStatus(id = id, status = BookStatus.from(status))

internal fun asExternalModel(entity: BookForQuoteEntity): BookForQuote =
    BookForQuote(id = entity.id, title = entity.title)
