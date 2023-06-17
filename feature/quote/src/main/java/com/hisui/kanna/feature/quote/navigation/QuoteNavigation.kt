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

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.hisui.kanna.feature.quote.EditQuoteRoute
import com.hisui.kanna.feature.quote.NewQuoteRoute
import com.hisui.kanna.feature.quote.QuoteListRoute
import com.hisui.kanna.feature.quote.QuoteRoute

const val quoteRoute = "quote_route"
const val quoteNavigationRoute = "quotes"
const val newQuoteNavigationRoute = "quote/new"

internal const val quoteIdArg = "quoteId"

internal class QuoteArgs(val quoteId: Long) {
    constructor(savedStateHandle: SavedStateHandle) :
        this(quoteId = checkNotNull(savedStateHandle[quoteIdArg]))
}

fun NavController.navigateToQuoteList(options: NavOptions?) {
    navigate(quoteNavigationRoute, options)
}

fun NavController.navigateToQuote(quoteId: Long) {
    val encodeId = Uri.encode(quoteId.toString())
    navigate("quote/$encodeId") {
        launchSingleTop = true
    }
}

fun NavController.navigateToEditQuote(quoteId: Long) {
    val encodeId = Uri.encode(quoteId.toString())
    navigate("quote/$encodeId/edit") {
        launchSingleTop = true
    }
}

fun NavGraphBuilder.quoteListScreen(
    navController: NavController,
    isCompact: Boolean,
    onOpenNewBookScreen: () -> Unit
) {
    navigation(
        route = quoteRoute,
        startDestination = quoteNavigationRoute
    ) {
        composable(route = quoteNavigationRoute) {
            QuoteListRoute(
                onOpenQuote = navController::navigateToQuote,
                onOpenNewQuoteScreen = { navController.navigate(newQuoteNavigationRoute) },
                onOpenNewBookScreen = onOpenNewBookScreen
            )
        }

        composable(route = newQuoteNavigationRoute) {
            NewQuoteRoute(
                isCompact = isCompact,
                popBackStack = navController::popBackStack
            )
        }

        composable(
            route = "quote/{$quoteIdArg}",
            arguments = listOf(navArgument(quoteIdArg) { type = NavType.LongType })
        ) {
            QuoteRoute(onOpenEdit = navController::navigateToEditQuote)
        }

        composable(
            route = "quote/{$quoteIdArg}/edit",
            arguments = listOf(navArgument(quoteIdArg) { type = NavType.LongType })
        ) {
            EditQuoteRoute(
                isCompact = isCompact,
                onOpenQuote = navController::navigateToQuote,
                onExit = navController::popBackStack
            )
        }
    }
}
