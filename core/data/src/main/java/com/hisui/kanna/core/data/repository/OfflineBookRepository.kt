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
import com.hisui.kanna.core.model.BookSorter
import com.hisui.kanna.core.model.Sort
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class OfflineBookRepository @Inject constructor(
    private val dao: BookDao,
    @Dispatcher(KannaDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : BookRepository {
    override suspend fun save(book: Book): Result<Unit> =
        withContext(ioDispatcher) {
            dao.insert(book.asEntity())
            Result.success(Unit)
        }

    override fun getAllStream(sort: Sort): Flow<List<Book>> =
        dao.getAllBooksAndAuthors(
            sortByTitle = sort.by == BookSorter.TITLE,
            sortByReadDate = sort.by == BookSorter.READ_DATE,
            isAsc = sort.direction.isAsc
        ).map(::asExternalModel)

    override fun getStream(id: Long): Flow<Book?> {
        TODO("Not yet implemented")
    }

    override suspend fun update(book: Book): Result<Unit> {
        TODO("Not yet implemented")
    }
}
