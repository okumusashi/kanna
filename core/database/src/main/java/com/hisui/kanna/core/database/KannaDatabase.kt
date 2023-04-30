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
import com.hisui.kanna.core.database.dao.FavouriteQuoteDao
import com.hisui.kanna.core.database.dao.GenreDao
import com.hisui.kanna.core.database.entity.AuthorEntity
import com.hisui.kanna.core.database.entity.BookEntity
import com.hisui.kanna.core.database.entity.FavouriteQuoteEntity
import com.hisui.kanna.core.database.entity.GenreEntity

@Database(
    entities = [
        BookEntity::class,
        AuthorEntity::class,
        GenreEntity::class,
        FavouriteQuoteEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(InstantTypeConverter::class)
abstract class KannaDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
    abstract fun authorDao(): AuthorDao
    abstract fun genreDao(): GenreDao
    abstract fun favouriteQuoteDao(): FavouriteQuoteDao
}
