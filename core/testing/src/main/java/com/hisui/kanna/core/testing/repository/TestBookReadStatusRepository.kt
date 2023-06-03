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

import com.hisui.kanna.core.data.repository.BookReadStatusRepository
import com.hisui.kanna.core.model.Book
import com.hisui.kanna.core.model.BookReadStatus

class TestBookReadStatusRepository : BookReadStatusRepository {

    private val _list: MutableList<BookReadStatus> =
        Book.Status.values().mapIndexed { i, status ->
            BookReadStatus(
                id = i.toLong() + 1,
                status = status
            )
        }.toMutableList()

    override suspend fun getAll(): List<BookReadStatus> = _list
}