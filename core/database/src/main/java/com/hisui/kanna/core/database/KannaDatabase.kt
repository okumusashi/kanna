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

package com.hisui.kanna.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hisui.kanna.core.database.dao.AuthorDao
import com.hisui.kanna.core.database.dao.BookDao
import com.hisui.kanna.core.database.dao.BookReadStatusDao
import com.hisui.kanna.core.database.dao.GenreDao
import com.hisui.kanna.core.database.dao.QuoteDao
import com.hisui.kanna.core.database.entity.AuthorEntity
import com.hisui.kanna.core.database.entity.BookEntity
import com.hisui.kanna.core.database.entity.BookReadStatusEntity
import com.hisui.kanna.core.database.entity.GenreEntity
import com.hisui.kanna.core.database.entity.QuoteEntity
import com.hisui.kanna.core.model.Book

@Database(
    entities = [
        BookEntity::class,
        BookReadStatusEntity::class,
        AuthorEntity::class,
        GenreEntity::class,
        QuoteEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(InstantTypeConverter::class)
abstract class KannaDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
    abstract fun bookReadStatusDao(): BookReadStatusDao
    abstract fun authorDao(): AuthorDao
    abstract fun genreDao(): GenreDao
    abstract fun favouriteQuoteDao(): QuoteDao
}

internal val PRE_POPULATE_QUERY: String =
    """
        INSERT INTO
            book_read_statuses (id, status)
        VALUES
            (1, '${Book.Status.HAVE_READ.name}'),
            (2, '${Book.Status.READING_NOW.name}'),
            (3, '${Book.Status.READ_NEXT.name}'),
            (4, '${Book.Status.WANT_TO_READ.name}');
    """.trimIndent()
