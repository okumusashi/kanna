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

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.navigation
import com.hisui.kanna.feature.book.navigation.newBookNavigationRoute
import com.hisui.kanna.feature.book.navigation.newBookScreen
import com.hisui.kanna.feature.home.navigation.homeNavigationRoute
import com.hisui.kanna.feature.home.navigation.homeScreen
import com.hisui.kanna.feature.quote.navigation.quoteScreen

@Composable
fun KannaNavHost(
    navController: NavHostController,
    windowSizeClass: WindowSizeClass
) {
    NavHost(
        navController = navController,
        startDestination = homeNavigationRoute
    ) {
        homeScreen(
            onNewBookFabClick = {
                navController.navigate(newBookNavigationRoute)
            }
        )
        quoteScreen()
        newBookScreen(
            isCompactScreen = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact,
            popBackStack = navController::popBackStack
        )
    }
}
