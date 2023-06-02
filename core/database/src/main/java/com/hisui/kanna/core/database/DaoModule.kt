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

import com.hisui.kanna.core.database.dao.AuthorDao
import com.hisui.kanna.core.database.dao.BookDao
import com.hisui.kanna.core.database.dao.BookReadStatusDao
import com.hisui.kanna.core.database.dao.FavouriteQuoteDao
import com.hisui.kanna.core.database.dao.GenreDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DaoModule {
    @Provides
    @Singleton
    fun providesBookDao(database: KannaDatabase): BookDao =
        database.bookDao()

    @Provides
    @Singleton
    fun provideBookReadStatusDao(database: KannaDatabase): BookReadStatusDao =
        database.bookReadStatusDao()

    @Provides
    @Singleton
    fun providesAuthorDao(database: KannaDatabase): AuthorDao =
        database.authorDao()

    @Provides
    @Singleton
    fun providesGenreDao(database: KannaDatabase): GenreDao =
        database.genreDao()

    @Provides
    @Singleton
    fun providesFavouriteQuoteDao(database: KannaDatabase): FavouriteQuoteDao =
        database.favouriteQuoteDao()
}
