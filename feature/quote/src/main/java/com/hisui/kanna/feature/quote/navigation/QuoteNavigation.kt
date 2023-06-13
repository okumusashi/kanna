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

package com.hisui.kanna.feature.quote.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.hisui.kanna.feature.quote.NewQuoteRoute
import com.hisui.kanna.feature.quote.QuoteListRoute

const val quoteRoute = "quote_route"
const val quoteNavigationRoute = "quotes"
const val newQuoteNavigationRoute = "quote/new"

fun NavController.navigateToQuoteList(options: NavOptions?) {
    navigate(quoteNavigationRoute, options)
}

fun NavGraphBuilder.quoteListScreen(
    navController: NavController,
    isWidthCompact: Boolean,
    isHeightCompact: Boolean,
    onOpenNewBookScreen: () -> Unit,
    popBackStack: () -> Unit
) {
    navigation(
        route = quoteRoute,
        startDestination = quoteNavigationRoute
    ) {
        composable(route = quoteNavigationRoute) {
            QuoteListRoute(
                onOpenNewQuoteScreen = { navController.navigate(newQuoteNavigationRoute) },
                onOpenNewBookScreen = onOpenNewBookScreen
            )
        }

        composable(route = newQuoteNavigationRoute) {
            NewQuoteRoute(
                isWidthCompact = isWidthCompact,
                isHeightCompact = isHeightCompact,
                popBackStack = popBackStack
            )
        }
    }
}
