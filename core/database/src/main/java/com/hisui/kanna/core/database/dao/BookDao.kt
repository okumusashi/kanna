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

package com.hisui.kanna.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.hisui.kanna.core.database.entity.AuthorEntity
import com.hisui.kanna.core.database.entity.BookEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {

    object BookColumn {
        const val TITLE = "title"
        const val READ_DATE = "read_date"
    }

    enum class SortBy(val columnName: String) {
        TITLE(BookColumn.TITLE),
        READ_DATE(BookColumn.READ_DATE)
    }

    @Insert
    suspend fun insert(vararg books: BookEntity)

    @Query(
        """
            SELECT * FROM books
            INNER JOIN authors ON books.author_id = authors.id
            ORDER BY
                CASE WHEN :isAsc = 1 THEN books.title END ASC,
                CASE WHEN :isAsc = 0 THEN books.title END DESC;
        """
    )
    fun getAllBooksAndAuthorsByTitle(isAsc: Boolean): Flow<Map<BookEntity, AuthorEntity>>

    @Query(
        """
            SELECT * FROM books
            INNER JOIN authors ON books.author_id = authors.id
            ORDER BY
                CASE WHEN :isAsc = 1 THEN books.read_date END ASC,
                CASE WHEN :isAsc = 1 THEN books.read_date END DESC;
        """
    )
    fun getAllBooksAndAuthorsByReadDate(isAsc: Boolean): Flow<Map<BookEntity, AuthorEntity>>
}
