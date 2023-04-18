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

import com.hisui.kanna.core.database.entity.AuthorEntity
import com.hisui.kanna.core.database.entity.BookEntity
import com.hisui.kanna.core.model.Book

internal fun Book.asEntity(): BookEntity =
    BookEntity(
        id = id,
        title = title,
        authorId = authorId,
        genre = genre,
        readDate = readDate,
        memo = memo,
        rating = rating
    )

internal fun asExternalModel(bookAndAuthor: Map<BookEntity, AuthorEntity>): List<Book> =
    bookAndAuthor.map { (book, author) ->
        Book(
            id = book.id,
            title = book.title,
            author = author.name,
            authorId = author.id,
            authorMemo = author.memo,
            genre = book.genre ?: "",
            readDate = book.readDate,
            memo = book.memo,
            rating = book.rating
        )
    }