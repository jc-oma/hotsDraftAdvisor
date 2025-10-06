package com.example.hotsdraftadviser.composables.searchbar

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.tooling.preview.Preview
import com.example.hotsdraftadviser.R
import com.example.hotsdraftadviser.RoleEnum
import com.example.hotsdraftadviser.composables.filter.getResponsiveFontSize

@Composable
fun ChampSearchBar(
    modifier: Modifier,
    searchQueryChamps: String,
    setRoleFilter: (RoleEnum?) -> Unit,
    updateChampSearchQuery: (String) -> Unit
) {
    OutlinedTextField(
        modifier = modifier,
        value = searchQueryChamps,
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
            if (searchQueryChamps.isNotEmpty()) {
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

@Preview
@Composable
private fun ChampSearchBarPreview() {
    ChampSearchBar(
        modifier = Modifier,
        searchQueryChamps = "Hello",
        setRoleFilter = {},
        updateChampSearchQuery = {}
    )
}