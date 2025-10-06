package com.example.hotsdraftadviser.composables.searchbar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.hotsdraftadviser.R
import com.example.hotsdraftadviser.RoleEnum
import com.example.hotsdraftadviser.composables.filter.getResponsiveFontSize

@Composable
fun RowScope.ChampSearchBar(
    searchQueryOwnTChamps: String,
    setRoleFilter: (RoleEnum?) -> Unit,
    updateChampSearchQuery: (String) -> Unit
) {
    OutlinedTextField(
        modifier = Modifier
            .padding(start = 8.dp, end = 8.dp)
            .weight(2f),
        value = searchQueryOwnTChamps,
        onValueChange = { newText: String ->
            setRoleFilter(null)
            updateChampSearchQuery(newText)
        },
        label = {
            Text(
                stringResource(R.string.main_activity_champs_suchen),
                fontSize = getResponsiveFontSize(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        textStyle = TextStyle(fontSize = getResponsiveFontSize()),
        trailingIcon = {
            if (searchQueryOwnTChamps.isNotEmpty()) {
                Icon(
                    Icons.Filled.Clear,
                    contentDescription = "Clear text",
                    modifier = Modifier.clickable {
                        updateChampSearchQuery("")
                    }
                )
            }
        }
    )
}