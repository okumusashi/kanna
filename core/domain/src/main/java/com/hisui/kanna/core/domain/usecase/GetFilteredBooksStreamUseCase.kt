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

package com.hisui.kanna.core.domain.usecase

import com.hisui.kanna.core.data.repository.BookRepository
import com.hisui.kanna.core.model.Book
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFilteredBooksStreamUseCase @Inject constructor(
    private val repository: BookRepository
) {
    operator fun invoke(q: String): Flow<List<Book>> =
        repository.getListStreamByQuery(q = q)
}
