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

package com.hisui.kanna.data

import com.hisui.kanna.core.database.entity.BookEntity
import kotlinx.datetime.Instant

internal fun testBookEntity(
    id: Long,
    title: String = "title",
    readDate: Instant = Instant.parse("2022-01-01T01:00:00Z")
) =
    BookEntity(
        _id = id,
        title = title,
        authorId = "author",
        genreId = "genre",
        readDate = readDate,
        thought = "thought",
        memo = "memo",
        rating = 1
    )
