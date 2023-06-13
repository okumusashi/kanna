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

package com.hisui.kanna.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.hisui.kanna.feature.book.navigation.bookScreen
import com.hisui.kanna.feature.book.navigation.navigateToBook
import com.hisui.kanna.feature.book.navigation.navigateToEditBook
import com.hisui.kanna.feature.book.navigation.navigateToNewBook
import com.hisui.kanna.feature.book.navigation.newBookScreen
import com.hisui.kanna.feature.home.navigation.homeNavigationRoute
import com.hisui.kanna.feature.home.navigation.homeScreen
import com.hisui.kanna.feature.quote.navigation.quoteListScreen

@Composable
fun KannaNavHost(
    navController: NavHostController,
    isWidthCompact: Boolean,
    isHeightCompact: Boolean
) {
    NavHost(
        navController = navController,
        startDestination = homeNavigationRoute
    ) {
        homeScreen(
            onNewBookFabClick = navController::navigateToNewBook,
            onOpenBook = navController::navigateToBook
        )

        quoteListScreen(
            navController = navController,
            isWidthCompact = isWidthCompact,
            isHeightCompact = isHeightCompact,
            onOpenNewBookScreen = navController::navigateToNewBook,
            popBackStack = navController::popBackStack
        )

        newBookScreen(
            isWidthCompact = isWidthCompact,
            isHeightCompact = isHeightCompact,
            popBackStack = navController::popBackStack
        )

        bookScreen(
            isWidthCompact = isWidthCompact,
            isHeightCompact = isHeightCompact,
            popBackStack = navController::popBackStack,
            onOpenBook = navController::navigateToBook,
            onOpenEditBook = navController::navigateToEditBook
        )
    }
}
