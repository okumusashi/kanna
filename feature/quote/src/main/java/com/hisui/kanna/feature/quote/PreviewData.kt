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

package com.hisui.kanna.feature.quote

import com.hisui.kanna.core.model.Quote
import kotlinx.datetime.Instant

private data class DummyQuote(
    val id: Long,
    val quote: String,
    val bookTitle: String,
    val author: String
)

private val dummyQuotes = listOf(
    DummyQuote(
        id = 1,
        quote = "It is only with the heart that one can see rightly; what is essential is invisible to the eye.",
        bookTitle = "The Little Prince",
        author = "Antoine de Saint-Exupéry"
    ),
    DummyQuote(
        id = 2,
        quote = "The most beautiful things in the world cannot be seen or touched, they are felt with the heart.",
        bookTitle = "The Little Prince",
        author = "Antoine de Saint-Exupéry"
    ),
    DummyQuote(
        id = 3,
        quote = "You become responsible, forever, for what you have tamed.",
        bookTitle = "The Little Prince",
        author = "Antoine de Saint-Exupéry"
    ),
    DummyQuote(
        id = 4,
        quote = "I am no bird; and no net ensnares me: I am a free human being with an independent will, which I now exert to leave you.",
        bookTitle = "Jane Eyre",
        author = "Charlotte Brontë"
    ),
    DummyQuote(
        id = 5,
        quote = "I would always rather be happy than dignified.",
        bookTitle = "Jane Eyre",
        author = "Charlotte Brontë"
    ),
    DummyQuote(
        id = 6,
        quote = "This above all: To thine own self be true, And it must follow, as the night the day, Thou canst not then be false to any man.",
        bookTitle = "Hamlet",
        author = "William Shakespeare"
    ),
    DummyQuote(
        id = 7,
        quote = "There is nothing either good or bad, but thinking makes it so.",
        bookTitle = "Hamlet",
        author = "William Shakespeare"
    ),
    DummyQuote(
        id = 8,
        quote = "Doubt thou the stars are fire; Doubt that the sun doth move; Doubt truth to be a liar; But never doubt I love.",
        bookTitle = "Hamlet",
        author = "William Shakespeare"
    ),
    DummyQuote(
        id = 9,
        quote = "Tomorrow I’ll think of some way to get him back. After all, tomorrow is another day.",
        bookTitle = "Gone with the Wind",
        author = "Margaret Mitchell"
    ),
    DummyQuote(
        id = 10,
        quote = "Don’t ever tell anybody anything. If you do, you start missing everybody.",
        bookTitle = "The Catcher in the Rye",
        author = "J.D. Salinger"
    ),
    DummyQuote(
        id = 11,
        quote = "I am no bird; and no net ensnares me: I am a free human being with an independent will, which I now exert to leave you.",
        bookTitle = "Jane Eyre",
        author = "Charlotte Brontë"
    ),
    DummyQuote(
        id = 12,
        quote = "I would always rather be happy than dignified.",
        bookTitle = "Jane Eyre",
        author = "Charlotte Brontë"
    ),
    DummyQuote(
        id = 13,
        quote = "The world breaks everyone, and afterward, many are strong at the broken places.",
        bookTitle = "A Farewell to Arms",
        author = "Ernest Hemingway"
    )
)

internal fun previewQuote(): Quote {
    val page = (1..200).random()
    val thought = "Here is my thought"
    val bookId = (1..100).random().toLong()
    val createdAt = Instant.parse("2023-03-0${randomInt}T0$randomInt:0$randomInt:00Z")
    val dummyQuote = dummyQuotes.random()

    return Quote(
        id = dummyQuote.id,
        page = page,
        quote = dummyQuote.quote,
        thought = thought,
        bookId = bookId,
        bookTitle = dummyQuote.bookTitle,
        authorId = dummyQuote.author,
        author = dummyQuote.author,
        createdAt = createdAt
    )
}

private val randomInt: Int = (1..9).random()
