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
import com.hisui.kanna.core.database.entity.QuoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuoteDao {
    @Insert
    suspend fun insert(entity: QuoteEntity)

    @Transaction
    @Query(
        """
            SELECT * FROM quotes
            INNER JOIN books ON quotes.book_id = books.id
            ORDER BY created_at DESC
        """
    )
    fun getAll(): Flow<Map<QuoteEntity, BookAndAuthorEntity>>

    @Transaction
    @Query(
        """
            SELECT * FROM quotes
            INNER JOIN books ON quotes.book_id = books.id
            WHERE quotes.id = :id
        """
    )
    fun getStream(id: Long): Flow<Map<QuoteEntity, BookAndAuthorEntity>>

    @Update
    fun update(entity: QuoteEntity)
}
