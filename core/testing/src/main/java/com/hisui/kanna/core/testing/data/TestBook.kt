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

package com.hisui.kanna.core.testing.data

import com.hisui.kanna.core.model.Author
import com.hisui.kanna.core.model.Book
import kotlinx.datetime.Clock

val defaultBook: Book =
    Book(
        id = 1,
        title = "title",
        readDate = Clock.System.now(),
        thought = "thought",
        memo = "memo",
        rating = 5,
        author = Author(
            id = "author1",
            name = "author1",
            memo = null,
            isFavourite = false
        ),
        genre = "genre1"
    )
