package com.example.hotsdraftadviser.composables.searchbar

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.hotsdraftadviser.R

@Composable
fun MapSearchBar(
    searchQueryMaps: String,
    updateMapsSearchQuery: (String) -> Unit,
    modifier: Modifier
) {
    OutlinedTextField(
        modifier = modifier,
        value = searchQueryMaps,
        onValueChange = { newText ->
            updateMapsSearchQuery(newText)
        },
        label = { Text(stringResource(R.string.main_activity_maps_suchen)) }
    )
}

@Preview
@Composable
private fun ChampSearchBarPreview() {
    Row {
        MapSearchBar(
            searchQueryMaps = "",
            updateMapsSearchQuery = {},
            modifier = Modifier.weight(1f)
        )
    }
}