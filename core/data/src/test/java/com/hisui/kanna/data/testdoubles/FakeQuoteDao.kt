/*
 * Copyright 2023 Lynn Sakashita
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

import com.hisui.kanna.core.database.dao.QuoteDao
import com.hisui.kanna.core.database.entity.BookAndAuthorEntity
import com.hisui.kanna.core.database.entity.QuoteEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeQuoteDao : QuoteDao {

    private val quotes = MutableStateFlow<Map<Long, QuoteEntity>>(emptyMap())
    private val booksAndAuthors = mutableMapOf<Long, BookAndAuthorEntity>()

    override suspend fun insert(entity: QuoteEntity) {
        quotes.update { it + (entity.id to entity) }
    }

    override fun getAll(): Flow<Map<QuoteEntity, BookAndAuthorEntity>> =
        quotes.map { quoteMap ->
            quoteMap.values.associateWith { quote ->
                booksAndAuthors[quote.bookId]!!
            }
        }

    override fun getStream(id: Long): Flow<Map<QuoteEntity, BookAndAuthorEntity>> =
        quotes.map { quoteMap ->
            quoteMap[id]
                ?.let { mapOf(it to booksAndAuthors[it.bookId]!!) }
                ?: emptyMap()
        }

    fun addBookAndAuthor(bookAndAuthor: BookAndAuthorEntity) {
        booksAndAuthors[bookAndAuthor.book.id] = bookAndAuthor
    }
}
