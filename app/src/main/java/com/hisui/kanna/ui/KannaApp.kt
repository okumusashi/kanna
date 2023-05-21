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

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.hisui.kanna.navigation.KannaNavHost
import com.hisui.kanna.navigation.KannaNavItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KannaApp(
    windowSizeClass: WindowSizeClass,
    appState: KannaAppState = rememberKannaAppState(windowSizeClass = windowSizeClass)
) {
    Scaffold(
        bottomBar = {
            if (appState.shouldShowBottomBar) {
                KannaBottomBar(
                    modifier = Modifier,
                    currentDestination = appState.currentDestination,
                    onNavigate = appState::navigateToTopLevelDestination
                )
            }
        }
    ) { paddingValues ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (appState.shouldShowNavRail) {
                KannaNavRail(
                    modifier = Modifier,
                    currentDestination = appState.currentDestination,
                    onNavigate = appState::navigateToTopLevelDestination
                )
            }

            KannaNavHost(
                navController = appState.navController,
                isWidthCompact = appState.isWidthCompact,
                isHeightCompact = appState.isHeightCompact
            )
        }
    }
}

@Composable
private fun KannaBottomBar(
    modifier: Modifier,
    destinations: List<KannaNavItem> = KannaNavItem.values().toList(),
    onNavigate: (KannaNavItem) -> Unit,
    currentDestination: NavDestination?
) {
    NavigationBar(modifier = modifier) {
        destinations.forEach { destination ->
            val selected = currentDestination.isTopLevelDestinationInHierarchy(destination)
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(destination) },
                icon = {
                    Icon(
                        imageVector = if (selected) destination.selectedIcon else destination.unselectedIcon,
                        contentDescription = null
                    )
                },
                label = { Text(stringResource(id = destination.titleRes)) }
            )
        }
    }
}

private fun NavDestination?.isTopLevelDestinationInHierarchy(destination: KannaNavItem) =
    this?.hierarchy?.any {
        it.route?.contains(destination.name, true) ?: false
    } ?: false

@Composable
private fun KannaNavRail(
    modifier: Modifier,
    destinations: List<KannaNavItem> = KannaNavItem.values().toList(),
    onNavigate: (KannaNavItem) -> Unit,
    currentDestination: NavDestination?
) {
    NavigationRail(modifier = modifier) {
        destinations.forEach { destination ->
            val selected = currentDestination.isTopLevelDestinationInHierarchy(destination)
            NavigationRailItem(
                selected = selected,
                onClick = { onNavigate(destination) },
                icon = {
                    Icon(
                        imageVector = if (selected) destination.selectedIcon else destination.unselectedIcon,
                        contentDescription = null
                    )
                },
                label = { Text(stringResource(id = destination.titleRes)) }
            )
        }
    }
}
