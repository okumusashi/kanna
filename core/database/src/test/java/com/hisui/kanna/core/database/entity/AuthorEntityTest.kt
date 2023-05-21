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

package com.hisui.kanna.core.database.entity

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class AuthorEntityTest {

    @Nested
    @DisplayName("constructor")
    inner class Constructor {

        @Test
        fun `GIVEN - name and non-null memo, WHEN - it is called, THEN - it should set id as name + memo`() {
            val name = "name"
            val memo = "memo"
            val expected = AuthorEntity(id = name + memo, name = name, memo = memo, isFavourite = false)
            val actual = AuthorEntity(name = name, memo = memo)
            assert(actual == expected)
        }

        @Test
        fun `GIVEN - name and memo as null, WHEN - it is called, THEN - it should set id same as name`() {
            val name = "name"
            val memo = null
            val expected = AuthorEntity(id = name, name = name, memo = memo, isFavourite = false)
            val actual = AuthorEntity(name = name, memo = memo)
            assert(actual == expected)
        }
    }
}
