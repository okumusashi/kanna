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

package com.hisui.kanna.core.data.di

import com.hisui.kanna.core.data.repository.AuthorRepository
import com.hisui.kanna.core.data.repository.BookRepository
import com.hisui.kanna.core.data.repository.GenreRepository
import com.hisui.kanna.core.data.repository.OfflineAuthorRepository
import com.hisui.kanna.core.data.repository.OfflineBookRepository
import com.hisui.kanna.core.data.repository.OfflineGenreRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
internal abstract class DataModule {

    @Binds
    abstract fun bindsBookRepository(
        impl: OfflineBookRepository
    ): BookRepository

    @Binds
    abstract fun bindsAuthorRepository(
        impl: OfflineAuthorRepository
    ): AuthorRepository

    @Binds
    abstract fun bindsGenreRepository(
        impl: OfflineGenreRepository
    ): GenreRepository
}
