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

package com.hisui.kanna.ui

import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.hisui.kanna.feature.home.navigation.navigateToHome
import com.hisui.kanna.feature.quote.navigation.navigateToQuote
import com.hisui.kanna.navigation.KannaNavItem

@Composable
fun rememberKannaAppState(
    navController: NavHostController = rememberNavController(),
    windowSizeClass: WindowSizeClass
): KannaAppState = remember {
    KannaAppState(
        navController = navController,
        windowSizeClass = windowSizeClass
    )
}

class KannaAppState(
    val navController: NavHostController,
    val windowSizeClass: WindowSizeClass
) {
    val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination

    val isWidthCompact: Boolean
        get() = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact

    val isHeightCompact: Boolean
        get() = windowSizeClass.heightSizeClass == WindowHeightSizeClass.Compact

    val shouldShowTwoPane: Boolean
        get() = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded

    val shouldShowBottomBar: Boolean get() = isWidthCompact

    val shouldShowNavRail: Boolean get() = !shouldShowBottomBar

    fun navigateToTopLevelDestination(destination: KannaNavItem) {
        val navOptions = navOptions {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            // Avoid multiple copies of the same destination when
            // reselecting the same item
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
            restoreState = true
        }
        when (destination) {
            KannaNavItem.HOME -> navController.navigateToHome(options = navOptions)
            KannaNavItem.QUOTE -> navController.navigateToQuote(options = navOptions)
            else -> {}
        }
    }
}
