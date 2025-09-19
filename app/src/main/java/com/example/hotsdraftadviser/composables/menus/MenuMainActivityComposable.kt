package com.example.hotsdraftadviser.composables.menus

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.CompareArrows
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun MenuMainActivityComposable(modifier: Modifier = Modifier, onDisclaymer: () -> Unit, onToggleListMode: () -> Unit) {
    var expanded by remember { mutableStateOf(true) }
    Box(
        modifier = modifier
            .padding(16.dp)
    ) {
        IconButton(onClick = { expanded = !expanded }) {
            Icon(Icons.Default.MoreVert, contentDescription = "More options")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Tutorial") },
                onClick = { /* Do something... */ }
            )
            DropdownMenuItem(
                text = { Text("Disclaim") },
                onClick = { onDisclaymer() }
            )
            // TODO
            //  drawable/skull_list_24"
            DropdownMenuItem(
                text = { Row{
                    Icon(Icons.AutoMirrored.Filled.List, contentDescription = "List")
                    Icon(Icons.Outlined.CompareArrows, contentDescription = "List")
                    Icon(Icons.Outlined.AccountBox, contentDescription = "List")
                } },
                onClick = { onToggleListMode() }
            )
        }
    }
}

@Preview
@Composable
fun MenuMainActivitPreview() {
    MenuMainActivityComposable(onDisclaymer = {}, onToggleListMode = {})
}