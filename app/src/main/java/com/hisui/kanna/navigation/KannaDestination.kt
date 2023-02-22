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

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.hisui.kanna.R

enum class KannaDestination(
    @DrawableRes val iconRes: Int,
    @StringRes val titleRes: Int
) {
    HOME(
        iconRes = R.drawable.ic_home,
        titleRes = R.string.home
    ),
    HISTORY(
        iconRes = R.drawable.ic_history,
        titleRes = R.string.history
    ),
    MY_PAGE(
        iconRes = R.drawable.ic_my_page,
        titleRes = R.string.my_page
    )
}
