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

package com.hisui.kanna.data.testdoubles

import com.hisui.kanna.core.database.dao.BookDao
import com.hisui.kanna.core.database.entity.AuthorEntity
import com.hisui.kanna.core.database.entity.BookAndAuthorEntity
import com.hisui.kanna.core.database.entity.BookEntity
import com.hisui.kanna.core.database.entity.BookReadStatusEntity
import com.hisui.kanna.core.model.BookStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeBookDao : BookDao {

    private val books = MutableStateFlow<Map<Long, BookEntity>>(emptyMap())

    override suspend fun insert(vararg books: BookEntity) {
        val newBooks = books.map { it.id to it }
        this.books.update { it + newBooks }
    }

    override fun getAllBooksAndAuthorsByTitle(isAsc: Boolean): Flow<List<BookAndAuthorEntity>> =
        books.map { bookMap ->
            bookMap.values
                .let { entities ->
                    if (isAsc) {
                        entities.sortedBy { it.title }
                    } else {
                        entities.sortedByDescending { it.title }
                    }
                }
                .map(::toBookAndAuthorEntity)
        }

    override fun getAllBooksAndAuthorsByReadDate(isAsc: Boolean): Flow<List<BookAndAuthorEntity>> =
        books.map { bookMap ->
            bookMap.values
                .let { entities ->
                    if (isAsc) {
                        entities.sortedBy { it.readDate }
                    } else {
                        entities.sortedByDescending { it.readDate }
                    }
                }
                .map(::toBookAndAuthorEntity)
        }

    override fun countStream(): Flow<Int> = books.map { it.size }

    override fun getBooksAndAuthorsByQuery(q: String): Flow<List<BookAndAuthorEntity>> =
        books.map { books ->
            books.values
                .filter { it.title.contains(q) }
                .map(::toBookAndAuthorEntity)
        }
}

private fun toBookAndAuthorEntity(book: BookEntity): BookAndAuthorEntity =
    BookAndAuthorEntity(
        book = book,
        author = AuthorEntity(
            id = book.authorId!!,
            name = book.authorId!!,
            memo = "",
            isFavourite = false
        ),
        status = BookReadStatusEntity(
            id = book.statusId!!,
            status = BookStatus.values()[book.statusId!!.toInt() - 1].name
        )
    )
