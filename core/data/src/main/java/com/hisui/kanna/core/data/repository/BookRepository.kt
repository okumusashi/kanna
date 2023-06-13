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

package com.hisui.kanna.core.data.repository

import com.hisui.kanna.core.model.Book
import com.hisui.kanna.core.model.BookForQuote
import com.hisui.kanna.core.model.BookForm
import com.hisui.kanna.core.model.BookSorter
import kotlinx.coroutines.flow.Flow

interface BookRepository {
    suspend fun save(book: BookForm): Result<Unit>

    fun getAllStream(sort: BookSorter, isAsc: Boolean): Flow<List<Book>>

    fun countStream(): Flow<Int>

    fun getStream(id: Long): Flow<Book?>

    fun getListForQuoteStreamByQuery(q: String): Flow<List<BookForQuote>>

    suspend fun update(id: Long, book: BookForm): Result<Unit>
}
