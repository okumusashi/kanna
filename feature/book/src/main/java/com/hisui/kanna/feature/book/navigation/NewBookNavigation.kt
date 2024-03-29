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

package com.hisui.kanna.feature.book.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.hisui.kanna.feature.book.NewBookRoute

const val newBookNavigationRoute = "book/new"

fun NavController.navigateToNewBook(options: NavOptions? = null) {
    navigate(newBookNavigationRoute, options)
}

fun NavGraphBuilder.newBookScreen(
    isWidthCompact: Boolean,
    isHeightCompact: Boolean,
    popBackStack: () -> Unit
) {
    composable(route = newBookNavigationRoute) {
        NewBookRoute(
            isWidthCompact = isWidthCompact,
            isHeightCompact = isHeightCompact,
            popBackStack = popBackStack
        )
    }
}
