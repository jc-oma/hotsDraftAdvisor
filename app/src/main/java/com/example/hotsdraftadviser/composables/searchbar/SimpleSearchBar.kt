package com.example.hotsdraftadviser.composables.searchbar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.text
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.hotsdraftadviser.R

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