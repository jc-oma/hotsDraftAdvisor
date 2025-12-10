package com.jcdevelopment.hotsdraftadviser.composables.menus

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.jcdevelopment.hotsdraftadviser.R

@Composable
fun MenuComposable(
    modifier: Modifier = Modifier,
    isStarRating: Boolean,
    isListMode: Boolean,
    onDisclaymer: () -> Unit,
    onToggleListMode: () -> Unit,
    onToggleStarRating: () -> Unit,
    onToggleStreaming: () -> Unit,
    onTutorial: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box() {
        IconButton(onClick = { expanded = !expanded }) {
            Icon(Icons.Default.MoreVert, contentDescription = "More options")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.main_acitivity_menu_tutorial)) },
                onClick = {
                    onTutorial()
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.main_acitivity_menu_disclaimer)) },
                onClick = {
                    onDisclaymer()
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.main_acitivity_menu_streaming_mode)) },
                onClick = {
                    onToggleStreaming()
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = {
                    Text(
                        if (isStarRating) stringResource(R.string.main_acitivity_menu_number_rating) else stringResource(
                            R.string.main_acitivity_menu_starrating
                        )
                    )
                },
                onClick = {
                    onToggleStarRating()
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = {
                    Row {
                        if (isListMode) {
                            Icon(Icons.Outlined.AccountBox, contentDescription = "List")
                        } else {
                            Icon(Icons.AutoMirrored.Filled.List, contentDescription = "List")
                        }
                    }
                },
                onClick = {
                    onToggleListMode()
                    expanded = false
                }
            )
        }
    }
}

@Preview
@Composable
fun MenuMainActivitPreview() {
    MenuComposable(
        onDisclaymer = {}, onToggleListMode = {}, onTutorial = {},
        onToggleStarRating = {}, isListMode = true, isStarRating = true,
        onToggleStreaming = {}
    )
}