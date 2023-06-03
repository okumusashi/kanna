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

import com.hisui.kanna.core.data.repository.AuthorRepository
import com.hisui.kanna.core.model.Author
import com.hisui.kanna.core.model.NewAuthor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class TestAuthorRepository : AuthorRepository {

    private val authors = MutableStateFlow<List<Author>>(emptyList())

    override fun getAllStream(): Flow<List<Author>> = authors

    override suspend fun save(author: NewAuthor): Result<Author> {
        val newAuthor = Author(
            id = author.name + author.memo,
            name = author.name,
            memo = author.memo,
            isFavourite = false
        )
        authors.update { it + newAuthor }
        return Result.success(newAuthor)
    }
}
