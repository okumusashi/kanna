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
import com.hisui.kanna.core.database.entity.QuoteEntity
import com.hisui.kanna.core.model.Quote
import com.hisui.kanna.core.model.QuoteForm

internal fun QuoteForm.toEntity(): QuoteEntity =
    QuoteEntity(
        bookId = bookId,
        page = page ?: 0,
        quote = quote,
        thought = thought
    )

internal fun Map<QuoteEntity, BookAndAuthorEntity>.asExternalModel(): List<Quote> =
    map { (quote, bookAndAuthor) ->
        quote.asExternalModel(bookAndAuthor = bookAndAuthor)
    }

internal fun QuoteEntity.asExternalModel(bookAndAuthor: BookAndAuthorEntity): Quote =
    Quote(
        id = id,
        page = page,
        quote = quote,
        thought = thought,
        createdAt = createdAt,
        bookId = bookAndAuthor.book.id,
        bookTitle = bookAndAuthor.book.title,
        authorId = bookAndAuthor.author.id,
        author = bookAndAuthor.author.name
    )

internal fun QuoteForm.asEntity(id: Long): QuoteEntity =
    QuoteEntity(
        _id = id,
        quote = quote,
        page = page ?: 0,
        bookId = bookId,
        thought = thought
    )
