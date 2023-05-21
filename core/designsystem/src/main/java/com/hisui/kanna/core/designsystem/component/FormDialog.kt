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

package com.hisui.kanna.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.hisui.kanna.core.designsystem.theme.KannaTheme

@Composable
fun FormDialog(
    modifier: Modifier = Modifier,
    title: String,
    confirmButtonText: String,
    dismissButtonText: String,
    onSubmit: () -> Unit,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {

    Dialog(onDismissRequest = onDismiss) {
        FormDialogContent(
            modifier = modifier,
            title = title,
            confirmButtonText = confirmButtonText,
            dismissButtonText = dismissButtonText,
            onSubmit = onSubmit,
            onDismiss = onDismiss,
            content = content
        )
    }
}

@Composable
private fun FormDialogContent(
    modifier: Modifier = Modifier,
    title: String,
    confirmButtonText: String,
    dismissButtonText: String,
    onDismiss: () -> Unit,
    onSubmit: () -> Unit,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(32.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        content()

        Row(
            modifier = Modifier.align(Alignment.End),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TextButton(onClick = onDismiss) {
                Text(text = dismissButtonText)
            }
            TextButton(
                onClick = {
                    onSubmit()
                    onDismiss()
                }
            ) {
                Text(text = confirmButtonText)
            }
        }
    }
}

@Preview
@Composable
private fun FormDialogPreview() {
    KannaTheme {
        FormDialogContent(
            title = "Create book",
            confirmButtonText = "Create",
            dismissButtonText = "Cancel",
            onDismiss = {},
            onSubmit = {}
        ) {
            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = { Text(text = "title") },
            )

            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = { Text(text = "author") },
            )
        }
    }
}
