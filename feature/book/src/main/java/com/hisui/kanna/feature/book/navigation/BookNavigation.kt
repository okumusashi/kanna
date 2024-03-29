/*
 * Copyright 2023 Lynn Sakashita
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

package com.hisui.kanna.feature.book.navigation

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.hisui.kanna.feature.book.BookRoute
import com.hisui.kanna.feature.book.EditBookRoute

internal const val bookIdArg = "bookId"

internal class BookArgs(val bookId: Long) {
    constructor(savedStateHandle: SavedStateHandle) :
        this(bookId = checkNotNull(savedStateHandle[bookIdArg]))
}

fun NavController.navigateToBook(bookId: Long) {
    val encodedId = Uri.decode(bookId.toString())
    navigate("book/$encodedId") {
        launchSingleTop = true
    }
}

fun NavController.navigateToEditBook(bookId: Long) {
    val encodedId = Uri.decode(bookId.toString())
    navigate("book/$encodedId/edit") {
        launchSingleTop = true
    }
}

fun NavGraphBuilder.bookScreen(
    isWidthCompact: Boolean,
    isHeightCompact: Boolean,
    popBackStack: () -> Unit,
    onOpenBook: (id: Long) -> Unit,
    onOpenEditBook: (id: Long) -> Unit
) {
    composable(
        route = "book/{$bookIdArg}",
        arguments = listOf(navArgument(bookIdArg) { type = NavType.LongType })
    ) {
        BookRoute(onOpenEdit = onOpenEditBook)
    }

    composable(
        route = "book/{$bookIdArg}/edit",
        arguments = listOf(navArgument(bookIdArg) { type = NavType.LongType })
    ) {
        EditBookRoute(
            isWidthCompact = isWidthCompact,
            isHeightCompact = isHeightCompact,
            popBackStack = popBackStack,
            openBook = onOpenBook
        )
    }
}
