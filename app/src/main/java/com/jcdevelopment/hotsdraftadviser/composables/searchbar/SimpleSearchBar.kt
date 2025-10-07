package com.jcdevelopment.hotsdraftadviser.composables.searchbar

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.jcdevelopment.hotsdraftadviser.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleSearchBar(
    modifier: Modifier = Modifier,
    onSearch: (String) -> Unit = {},
    searchResults: List<String> = emptyList(),
    searchQueryMaps: String,
    updateMapsSearchQuery: (String) -> Unit
) {
    val initialText = stringResource(R.string.main_activity_maps_suchen)
    // Controls expansion state of the search bar
    var expanded by rememberSaveable { mutableStateOf(false) }

    SearchBar(
        modifier = modifier,
        inputField = {
            SearchBarDefaults.InputField(
                query = searchQueryMaps,
                onQueryChange = { updateMapsSearchQuery(it) },
                onSearch = { expanded = false },
                expanded = expanded,
                onExpandedChange = { expanded = false },
                placeholder = { initialText },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") }
            )
        },
        expanded = expanded,
        onExpandedChange = { expanded = false },
    ) {}
}


@Preview
@Composable
private fun SimpleSearchBarPreview() {
    val textFieldState = TextFieldState()
    SimpleSearchBar(
        onSearch = {},
        searchResults = listOf("Result 1", "Result 2", "Result 3"),
        modifier = Modifier,
        searchQueryMaps = "Maps suchen ...",
        updateMapsSearchQuery = {}
    )
}