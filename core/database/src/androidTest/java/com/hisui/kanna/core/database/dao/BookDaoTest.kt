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
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import com.hisui.kanna.core.database.KannaDatabase
import com.hisui.kanna.core.database.PRE_POPULATE_QUERY
import com.hisui.kanna.core.database.entity.AuthorEntity
import com.hisui.kanna.core.database.entity.BookAndAuthorEntity
import com.hisui.kanna.core.database.entity.BookEntity
import com.hisui.kanna.core.database.entity.BookForQuoteEntity
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
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    db.execSQL(PRE_POPULATE_QUERY)
                }
            })
            .build()

        dao = database.bookDao()

        runTest {
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
        runTest { database.authorDao().insert(testAuthor()) }
        val book1 = testBookEntity(id = 1, title = "a", statusId = status.id)
        val book2 = testBookEntity(id = 2, title = "c", statusId = status.id)
        val book3 = testBookEntity(id = 3, title = "b", statusId = status.id)
        runTest { dao.insert(book1, book2, book3) }

        runTest {
            // WHEN
            val actual = dao.getAllBooksAndAuthorsByTitle(isAsc = true).first()

            // THEN
            val expected = listOf(
                BookAndAuthorEntity(book = book1, author = testAuthor(), status = status),
                BookAndAuthorEntity(book = book3, author = testAuthor(), status = status),
                BookAndAuthorEntity(book = book2, author = testAuthor(), status = status)
            )
            assertThat(actual)
                .containsExactlyElementsIn(expected)
                .inOrder()
        }
    }

    @Test
    fun getAllBooksAndAuthorsByTitle_descending() {
        // GIVEN
        runTest { database.authorDao().insert(testAuthor()) }
        val book1 = testBookEntity(id = 1, title = "a", statusId = status.id)
        val book2 = testBookEntity(id = 2, title = "c", statusId = status.id)
        val book3 = testBookEntity(id = 3, title = "b", statusId = status.id)
        runTest { dao.insert(book1, book2, book3) }

        runTest {
            // WHEN
            val actual = dao.getAllBooksAndAuthorsByTitle(isAsc = false).first()

            // THEN
            val expected = listOf(
                BookAndAuthorEntity(book = book2, author = testAuthor(), status = status),
                BookAndAuthorEntity(book = book3, author = testAuthor(), status = status),
                BookAndAuthorEntity(book = book1, author = testAuthor(), status = status)
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
        runTest { database.authorDao().insert(testAuthor()) }
        val book1 = testBookEntity(id = 1, readDate = Instant.parse("2023-01-01T00:00:00.00Z"), statusId = status.id)
        val book2 = testBookEntity(id = 2, readDate = Instant.parse("2023-03-01T00:00:00.00Z"), statusId = status.id)
        val book3 = testBookEntity(id = 3, readDate = Instant.parse("2023-02-01T00:00:00.00Z"), statusId = status.id)
        runTest { dao.insert(book1, book2, book3) }

        runTest {
            // WHEN
            val actual = dao.getAllBooksAndAuthorsByReadDate(isAsc = true).first()

            // THEN
            val expected = listOf(
                BookAndAuthorEntity(book = book1, author = testAuthor(), status = status),
                BookAndAuthorEntity(book = book3, author = testAuthor(), status = status),
                BookAndAuthorEntity(book = book2, author = testAuthor(), status = status)
            )
            assertThat(actual)
                .containsExactlyElementsIn(expected)
                .inOrder()
        }
    }

    @Test
    fun getAllBooksAndAuthorsByReadDate_descending() {
        // GIVEN
        runTest { database.authorDao().insert(testAuthor()) }
        val book1 = testBookEntity(id = 1, readDate = Instant.parse("2023-01-01T00:00:00.00Z"), statusId = status.id)
        val book2 = testBookEntity(id = 2, readDate = Instant.parse("2023-03-01T00:00:00.00Z"), statusId = status.id)
        val book3 = testBookEntity(id = 3, readDate = Instant.parse("2023-02-01T00:00:00.00Z"), statusId = status.id)
        runTest { dao.insert(book1, book2, book3) }

        runTest {
            // WHEN
            val actual = dao.getAllBooksAndAuthorsByReadDate(isAsc = false).first()

            // THEN
            val expected = listOf(
                BookAndAuthorEntity(book = book2, author = testAuthor(), status = status),
                BookAndAuthorEntity(book = book3, author = testAuthor(), status = status),
                BookAndAuthorEntity(book = book1, author = testAuthor(), status = status)
            )
            assertThat(actual)
                .containsExactlyElementsIn(expected)
                .inOrder()
        }
    }
    // endregion

    // regin #getBookForQuoteStreamByQuery
    @Test
    fun getBookForQuoteStreamByQuery_hit_none() {
        // GIVEN
        val q = "d"

        // WHEN
        //  - books with all different names
        //  - all the books have the same author whose name doesn't contain any of same character in any books' titles
        val author = testAuthor(name = "x")
        runTest { database.authorDao().insert(author) }
        val book1 = testBookEntity(id = 1, title = "a", _authorId = author.id)
        val book2 = testBookEntity(id = 2, title = "b", _authorId = author.id)
        runTest { dao.insert(book1, book2) }

        runTest {
            val actual = dao.getBookForQuoteStreamByQuery(q = q).first()

            // THEN
            assertThat(actual).isEmpty()
        }
    }

    @Test
    fun getBookForQuoteStreamByQuery_hit_1_record_by_title() {
        // GIVEN - tu hit the title of book2
        val q = "b"

        // WHEN
        //  - books with all different names
        //  - all the books have the same author whose name doesn't contain any of same character in any books' titles
        val author = testAuthor(id = "xyz", name = "xyz")
        runTest { database.authorDao().insert(author) }
        val book1 = testBookEntity(id = 1, title = "abc", _authorId = author.id)
        val book2 = testBookEntity(id = 2, title = "def", _authorId = author.id)
        runTest { dao.insert(book1, book2) }

        runTest {
            val actual = dao.getBookForQuoteStreamByQuery(q = q).first()

            // THEN
            val expected = listOf(
                BookForQuoteEntity(id = book1.id, title = "${book1.title} (${author.name})")
            )
            assertThat(actual).isEqualTo(expected)
        }
    }

    @Test
    fun getBookForQuoteStreamByQuery_hit_1_record_by_author() {
        // GIVEN - to hit authorName2
        val q = "v"

        // WHEN
        //  - there are 2 authors with different names
        val author1 = testAuthor(id = "xyz", name = "xyz")
        val author2 = testAuthor(id = "uvw", name = "uvw")

        runTest {
            database.authorDao().apply {
                insert(author1)
                insert(author2)
            }
        }

        // WHEN
        //  - there are 2 books with different names and different authors
        val book1 = testBookEntity(id = 1, title = "abc", _authorId = author1.id)
        val book2 = testBookEntity(id = 2, title = "def", _authorId = author2.id)
        runTest { dao.insert(book1, book2) }

        runTest {
            val actual = dao.getBookForQuoteStreamByQuery(q = q).first()

            // THEN
            val expected = listOf(
                BookForQuoteEntity(id = book2.id, title = "${book2.title} (${author2.name})")
            )
            assertThat(actual).isEqualTo(expected)
        }
    }

    @Test
    fun getBookForQuoteStreamByQuery_hit_1_record_by_author_and_1_record_by_title() {
        // GIVEN - to hit the title of book 1 and the name of author2
        val q = "b"

        // WHEN
        //  - there are 2 authors with different names
        val author1 = testAuthor(id = "xyz", name = "xyz")
        val author2 = testAuthor(id = "uvb", name = "uvb")
        val author3 = testAuthor(id = "rst", name = "rst")

        runTest {
            database.authorDao().apply {
                insert(author1)
                insert(author2)
                insert(author3)
            }
        }

        // WHEN
        //  - there are 2 books with different names and different authors
        val book1 = testBookEntity(id = 1, title = "abc", _authorId = author1.id)
        val book2 = testBookEntity(id = 2, title = "def", _authorId = author2.id)
        val book3 = testBookEntity(id = 3, title = "ghi", _authorId = author3.id)
        runTest { dao.insert(book1, book2, book3) }

        runTest {
            val actual = dao.getBookForQuoteStreamByQuery(q = q).first()

            // THEN
            val expected = listOf(
                BookForQuoteEntity(id = book1.id, title = "${book1.title} (${author1.name})"),
                BookForQuoteEntity(id = book2.id, title = "${book2.title} (${author2.name})")
            )
            assertThat(actual).isEqualTo(expected)
        }
    }
}

private const val authorId = "author"
private fun testAuthor(
    id: String = authorId,
    name: String = authorId
) = AuthorEntity(
    id = id,
    name = name,
    memo = "",
    isFavourite = false
)

private const val genre = "genre"

private fun testBookEntity(
    id: Long,
    title: String = "test",
    readDate: Instant = Instant.parse("2023-01-01T00:00:00.00Z"),
    statusId: Long = 1,
    _authorId: String = authorId
): BookEntity =
    BookEntity(
        id = id,
        title = title,
        authorId = _authorId,
        genreId = genre,
        statusId = statusId,
        readDate = readDate,
        thought = "thought",
        memo = "testMemo",
        rating = 5
    )
