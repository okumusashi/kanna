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

package com.hisui.kanna.core.testing.repository

import com.hisui.kanna.core.data.repository.BookRepository
import com.hisui.kanna.core.model.Book
import com.hisui.kanna.core.model.BookSorter
import com.hisui.kanna.core.model.Sort
import com.hisui.kanna.core.model.SortDirection
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class TestBookRepository : BookRepository {
    private val books = MutableStateFlow<Map<Long, Book>>(emptyMap())

    override suspend fun save(book: Book): Result<Unit> {
        updateBook(book = book)
        return Result.success(Unit)
    }

    override fun getAllStream(sort: Sort): Flow<List<Book>> =
        books.map { map ->
            map.values.toList().let { books ->
                when (sort.by) {
                    BookSorter.READ_DATE ->
                        if (sort.direction == SortDirection.ASC) books.sortedBy { it.readDate }
                        else books.sortedByDescending { it.readDate }

                    BookSorter.TITLE ->
                        if (sort.direction == SortDirection.ASC) books.sortedBy { it.title }
                        else books.sortedByDescending { it.title }

                    else -> books
                }
            }
        }

    override fun getStream(id: Long): Flow<Book?> =
        books.map { books ->
            books.getOrDefault(id, null)
        }

    override suspend fun update(book: Book): Result<Unit> = save(book = book)

    private fun updateBook(book: Book) {
        books.update {
            it.toMutableMap().apply {
                this[book.id] = book
            }.toMap()
        }
    }
}