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

package com.hisui.kanna.core.model

import kotlinx.datetime.Instant

data class Book(
    val id: Long,
    val title: String,
    val readDate: Instant,
    val memo: String,
    val thought: String,
    val rating: Int,
    val author: Author,
    val genre: String,
    val status: Status
) {
    enum class Status {
        HAVE_READ,
        READING_NOW,
        READ_NEXT,
        WANT_TO_READ
        ;

        companion object {
            fun from(value: String): Status =
                values().find { it.name == value } ?: HAVE_READ
        }
    }
}

enum class BookSorter {
    TITLE, READ_DATE
}
