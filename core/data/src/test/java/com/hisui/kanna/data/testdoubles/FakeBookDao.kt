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
import com.hisui.kanna.core.database.entity.BookEntity
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

    override fun getAllBooksAndAuthorsByTitle(isAsc: Boolean): Flow<Map<BookEntity, AuthorEntity>> =
        books.map { bookMap ->
            bookMap.values
                .let { entities ->
                    if (isAsc) entities.sortedBy { it.title }
                    else entities.sortedByDescending { it.title }
                }
                .associateWith(::toTestAuthorEntity)
        }

    override fun getAllBooksAndAuthorsByReadDate(isAsc: Boolean): Flow<Map<BookEntity, AuthorEntity>> =
        books.map { bookMap ->
            bookMap.values
                .let { entities ->
                    if (isAsc) entities.sortedBy { it.readDate }
                    else entities.sortedByDescending { it.readDate }
                }
                .associateWith(::toTestAuthorEntity)
        }
}

private fun toTestAuthorEntity(book: BookEntity): AuthorEntity =
    AuthorEntity(
        id = book.authorId!!,
        name = book.authorId!!,
        memo = "",
        isFavourite = false
    )
