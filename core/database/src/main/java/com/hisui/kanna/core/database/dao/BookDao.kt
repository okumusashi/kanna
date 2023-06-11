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
import androidx.room.Transaction
import androidx.room.Update
import com.hisui.kanna.core.database.entity.BookAndAuthorEntity
import com.hisui.kanna.core.database.entity.BookAndAuthorEntityWithQuotes
import com.hisui.kanna.core.database.entity.BookEntity
import com.hisui.kanna.core.database.entity.BookForQuoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {

    @Insert
    suspend fun insert(vararg books: BookEntity)

    @Transaction
    @Query(
        """
            SELECT * FROM books
            ORDER BY
                CASE WHEN :isAsc = 1 THEN books.title END ASC,
                CASE WHEN :isAsc = 0 THEN books.title END DESC;
        """
    )
    fun getAllBooksAndAuthorsByTitle(isAsc: Boolean): Flow<List<BookAndAuthorEntity>>

    @Transaction
    @Query(
        """
            SELECT * FROM books
            ORDER BY
                CASE WHEN :isAsc = 1 THEN books.title END ASC,
                CASE WHEN :isAsc = 0 THEN books.title END DESC;
        """
    )
    fun getAllBooksAndAuthorsByReadDate(isAsc: Boolean): Flow<List<BookAndAuthorEntity>>

    @Transaction
    @Query(
        """
            SELECT
                books.id,
                books.title || ' (' || authors.id || ')' as title
            FROM books
            INNER JOIN authors ON books.author_id = authors.id
            WHERE books.title LIKE '%' || :q || '%'
            OR authors.name LIKE '%' || :q || '%'
        """
    )
    fun getBookForQuoteStreamByQuery(q: String): Flow<List<BookForQuoteEntity>>

    @Query("SELECT COUNT(1) FROM books")
    fun countStream(): Flow<Int>

    @Transaction
    @Query("SELECT * FROM books WHERE books.id = :id")
    fun getStreamWithQuotes(id: Long): Flow<BookAndAuthorEntityWithQuotes?>

    @Update
    fun update(book: BookEntity)
}
