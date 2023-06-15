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

package com.hisui.kanna.core.testing.repository

import com.hisui.kanna.core.data.repository.QuoteRepository
import com.hisui.kanna.core.model.Book
import com.hisui.kanna.core.model.Quote
import com.hisui.kanna.core.model.QuoteForm
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.datetime.Clock

class TestQuoteRepository : QuoteRepository {

    private val quotes = MutableStateFlow<Map<Long, Quote>>(emptyMap())

    private val books = mutableMapOf<Long, Book>()

    override suspend fun save(quote: QuoteForm): Result<Unit> {
        quotes.update { state ->
            val id = state.keys.maxOrNull()?.plus(1) ?: 1
            quote.asQuote(
                id = id,
                book = books[quote.bookId]!!
            ).let {
                state + (id to it)
            }
        }
        return Result.success(Unit)
    }

    override fun getAllStream(): Flow<List<Quote>> = quotes.map { it.values.toList() }

    override fun getStream(id: Long): Flow<Quote?> = quotes.map { it[id] }
    override suspend fun update(id: Long, quote: QuoteForm): Result<Unit> {
        quotes.update {
            it + mapOf(id to quote.asQuote(id = id, book = books[quote.bookId]!!))
        }
        return Result.success(Unit)
    }

    fun addBook(book: Book) {
        books[book.id] = book
    }

    private fun QuoteForm.asQuote(id: Long, book: Book): Quote =
        Quote(
            id = id,
            page = page ?: 0,
            quote = quote,
            thought = thought,
            createdAt = Clock.System.now(),
            bookId = bookId,
            bookTitle = book.title,
            authorId = book.author.id,
            author = book.author.name
        )
}
