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

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * [id] is basically the [name].
 *
 * If there are multiple authors with the same name, they are distinguished by [id].
 * In such case, the [id] would be [name] + [memo].
 * [memo] can be anything that can distinguish the authors, which is edited by a user.
 */
@Entity(tableName = "authors")
data class AuthorEntity(
    @PrimaryKey val id: String,
    val name: String,
    val memo: String?,
    @ColumnInfo(name = "is_favourite") val isFavourite: Boolean
) {
    constructor(name: String, memo: String?) : this(
        id = memo?.let { name + memo } ?: name,
        name = name,
        memo = memo,
        isFavourite = false
    )
}
