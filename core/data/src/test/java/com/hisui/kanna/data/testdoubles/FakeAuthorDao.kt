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

import com.hisui.kanna.core.database.dao.AuthorDao
import com.hisui.kanna.core.database.entity.AuthorEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeAuthorDao : AuthorDao {
    private val authors = MutableStateFlow<Map<String, AuthorEntity>>(emptyMap())

    override suspend fun insert(author: AuthorEntity) {
        val newAuthor = author.id to author
        authors.update { it + newAuthor }
    }

    override fun getAllStream(): Flow<List<AuthorEntity>> = authors.map { it.values.toList() }
}
