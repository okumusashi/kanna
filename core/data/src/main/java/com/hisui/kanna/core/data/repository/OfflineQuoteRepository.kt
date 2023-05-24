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
import com.hisui.kanna.core.data.mapper.asExternalModel
import com.hisui.kanna.core.data.mapper.toEntity
import com.hisui.kanna.core.database.dao.FavouriteQuoteDao
import com.hisui.kanna.core.model.NewQuote
import com.hisui.kanna.core.model.Quote
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class OfflineQuoteRepository @Inject constructor(
    private val dao: FavouriteQuoteDao,
    @Dispatcher(KannaDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) : QuoteRepository {
    override suspend fun save(quote: NewQuote): Result<Unit> {
        withContext(ioDispatcher) {
            dao.insert(quote.toEntity())
        }
        return Result.success(Unit)
    }

    override fun getAllStream(): Flow<List<Quote>> =
        dao.getAll().map { quotes ->
            quotes.asExternalModel()
        }
}
