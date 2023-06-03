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

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import com.hisui.kanna.core.database.KannaDatabase
import com.hisui.kanna.core.database.PRE_POPULATE_QUERY
import com.hisui.kanna.core.database.entity.AuthorEntity
import com.hisui.kanna.core.database.entity.BookAndAuthorEntity
import com.hisui.kanna.core.database.entity.BookEntity
import com.hisui.kanna.core.database.entity.BookReadStatusEntity
import com.hisui.kanna.core.database.entity.GenreEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BookDaoTest {

    private lateinit var dao: BookDao
    private lateinit var database: KannaDatabase

    private lateinit var status: BookReadStatusEntity

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        database = Room
            .inMemoryDatabaseBuilder(context, KannaDatabase::class.java)
            .build()

        dao = database.bookDao()

        runTest {
            database.authorDao().insert(testAuthor)
            database.query(query = PRE_POPULATE_QUERY, args = null)
            database.genreDao().insert(GenreEntity(genre = genre, isFavourite = false))

            status = database.bookReadStatusDao().getAll().first()
        }
    }

    @After
    fun tearDown() {
        database.close()
    }

    // region #getAllBoosAndAuthorsByTitle
    @Test
    fun getAllBooksAndAuthorsByTitle_ascending() {
        // GIVEN
        val book1 = testBookEntity(id = 1, title = "a", statusId = status.id)
        val book2 = testBookEntity(id = 2, title = "c", statusId = status.id)
        val book3 = testBookEntity(id = 3, title = "b", statusId = status.id)
        runTest { dao.insert(book1, book2, book3) }

        runTest {
            // WHEN
            val actual = dao.getAllBooksAndAuthorsByTitle(isAsc = true).first()

            // THEN
            val expected = listOf(
                BookAndAuthorEntity(book = book1, author = testAuthor, status = status),
                BookAndAuthorEntity(book = book3, author = testAuthor, status = status),
                BookAndAuthorEntity(book = book2, author = testAuthor, status = status)
            )
            assertThat(actual)
                .containsExactlyElementsIn(expected)
                .inOrder()
        }
    }

    @Test
    fun getAllBooksAndAuthorsByTitle_descending() {
        // GIVEN
        val book1 = testBookEntity(id = 1, title = "a", statusId = status.id)
        val book2 = testBookEntity(id = 2, title = "c", statusId = status.id)
        val book3 = testBookEntity(id = 3, title = "b", statusId = status.id)
        runTest { dao.insert(book1, book2, book3) }

        runTest {
            // WHEN
            val actual = dao.getAllBooksAndAuthorsByTitle(isAsc = false).first()

            // THEN
            val expected = listOf(
                BookAndAuthorEntity(book = book2, author = testAuthor, status = status),
                BookAndAuthorEntity(book = book3, author = testAuthor, status = status),
                BookAndAuthorEntity(book = book1, author = testAuthor, status = status)
            )
            assertThat(actual)
                .containsExactlyElementsIn(expected)
                .inOrder()
        }
    }
    // endregion

    // region #getAllBoosAndAuthorsByReadDate
    @Test
    fun getAllBooksAndAuthorsByReadDate_ascending() {
        // GIVEN
        val book1 = testBookEntity(id = 1, readDate = Instant.parse("2023-01-01T00:00:00.00Z"), statusId = status.id)
        val book2 = testBookEntity(id = 2, readDate = Instant.parse("2023-03-01T00:00:00.00Z"), statusId = status.id)
        val book3 = testBookEntity(id = 3, readDate = Instant.parse("2023-02-01T00:00:00.00Z"), statusId = status.id)
        runTest { dao.insert(book1, book2, book3) }

        runTest {
            // WHEN
            val actual = dao.getAllBooksAndAuthorsByReadDate(isAsc = true).first()

            // THEN
            val expected = listOf(
                BookAndAuthorEntity(book = book1, author = testAuthor, status = status),
                BookAndAuthorEntity(book = book3, author = testAuthor, status = status),
                BookAndAuthorEntity(book = book2, author = testAuthor, status = status)
            )
            assertThat(actual)
                .containsExactlyElementsIn(expected)
                .inOrder()
        }
    }

    @Test
    fun getAllBooksAndAuthorsByReadDate_descending() {
        // GIVEN
        val book1 = testBookEntity(id = 1, readDate = Instant.parse("2023-01-01T00:00:00.00Z"), statusId = status.id)
        val book2 = testBookEntity(id = 2, readDate = Instant.parse("2023-03-01T00:00:00.00Z"), statusId = status.id)
        val book3 = testBookEntity(id = 3, readDate = Instant.parse("2023-02-01T00:00:00.00Z"), statusId = status.id)
        runTest { dao.insert(book1, book2, book3) }

        runTest {
            // WHEN
            val actual = dao.getAllBooksAndAuthorsByReadDate(isAsc = false).first()

            // THEN
            val expected = listOf(
                BookAndAuthorEntity(book = book2, author = testAuthor, status = status),
                BookAndAuthorEntity(book = book3, author = testAuthor, status = status),
                BookAndAuthorEntity(book = book1, author = testAuthor, status = status)
            )
            assertThat(actual)
                .containsExactlyElementsIn(expected)
                .inOrder()
        }
    }
    // endregion
}

private const val authorId = "author"
private val testAuthor = AuthorEntity(
    id = authorId,
    name = authorId,
    memo = "",
    isFavourite = false
)

private const val genre = "genre"

private fun testBookEntity(
    id: Long,
    title: String = "test",
    readDate: Instant = Instant.parse("2023-01-01T00:00:00.00Z"),
    statusId: Long = 1
): BookEntity =
    BookEntity(
        id = id,
        title = title,
        authorId = authorId,
        genreId = genre,
        statusId = statusId,
        readDate = readDate,
        thought = "thought",
        memo = "testMemo",
        rating = 5
    )
