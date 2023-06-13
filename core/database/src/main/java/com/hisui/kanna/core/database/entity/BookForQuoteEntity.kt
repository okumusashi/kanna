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

package com.hisui.kanna.core.database.entity

/**
 * This entity is for the result of the query to get the book list for new quote.
 *
 * @param id is [BookEntity.id]
 * @param title is [BookEntity.title] and [AuthorEntity.name]
 */
data class BookForQuoteEntity(
    val id: Long,
    val title: String
)
