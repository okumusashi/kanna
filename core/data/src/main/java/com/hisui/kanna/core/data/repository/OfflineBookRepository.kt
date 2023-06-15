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

import com.hisui.kanna.core.common.Dispatcher
import com.hisui.kanna.core.common.KannaDispatchers
import com.hisui.kanna.core.data.mapper.asEntity
import com.hisui.kanna.core.data.mapper.asExternalModel
import com.hisui.kanna.core.database.dao.BookDao
import com.hisui.kanna.core.model.Book
import com.hisui.kanna.core.model.BookForQuote
import com.hisui.kanna.core.model.BookForm
import com.hisui.kanna.core.model.BookSorter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class OfflineBookRepository @Inject constructor(
    private val dao: BookDao,
    @Dispatcher(KannaDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) : BookRepository {
    override suspend fun save(book: BookForm): Result<Unit> =
        withContext(ioDispatcher) {
            dao.insert(book.asEntity())
            Result.success(Unit)
        }

    override fun getAllStream(sort: BookSorter, isAsc: Boolean): Flow<List<Book>> =
        when (sort) {
            BookSorter.TITLE ->
                dao.getAllBooksAndAuthorsByTitle(isAsc = isAsc)
            BookSorter.READ_DATE ->
                dao.getAllBooksAndAuthorsByReadDate(isAsc = isAsc)
        }.map { it.asExternalModel() }

    override fun countStream(): Flow<Int> = dao.countStream()

    override fun getStream(id: Long): Flow<Book?> =
        dao.getStreamWithQuotes(id = id).map { it?.asExternalModel() }

    override suspend fun getForQuote(id: Long): BookForQuote =
        withContext(ioDispatcher) {
            dao.getForQuote(id = id).let(::asExternalModel)
        }

    override fun getListForQuoteStreamByQuery(q: String): Flow<List<BookForQuote>> =
        dao.getBookForQuoteStreamByQuery(q = q).map { list ->
            list.map(::asExternalModel)
        }

    override suspend fun update(id: Long, book: BookForm): Result<Unit> =
        withContext(ioDispatcher) {
            dao.update(book.asEntity(id = id))
            Result.success(Unit)
        }
}
