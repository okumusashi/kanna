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

package com.hisui.kanna.core.data.repository

import com.hisui.kanna.core.common.Dispatcher
import com.hisui.kanna.core.common.KannaDispatchers
import com.hisui.kanna.core.data.mapper.asExternalModel
import com.hisui.kanna.core.database.dao.BookReadStatusDao
import com.hisui.kanna.core.model.BookReadStatus
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class OfflineBookReadStatusRepository @Inject constructor(
    private val dao: BookReadStatusDao,
    @Dispatcher(KannaDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) : BookReadStatusRepository {
    override suspend fun getAll(): List<BookReadStatus> =
        withContext(ioDispatcher) {
            dao.getAll().map(::asExternalModel)
        }
}
