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

package com.hisui.kanna.core.data.mapper

import com.hisui.kanna.core.database.entity.AuthorEntity
import com.hisui.kanna.core.model.Author
import com.hisui.kanna.core.model.AuthorInput

internal fun AuthorInput.asEntity(): AuthorEntity =
    AuthorEntity(name = name, memo = memo)

internal fun asExternalModel(list: List<AuthorEntity>): List<Author> =
    list.map { it.asExternalModel() }

internal fun AuthorEntity.asExternalModel(): Author =
    Author(
        id = id,
        name = name,
        memo = memo,
        isFavourite = isFavourite
    )
