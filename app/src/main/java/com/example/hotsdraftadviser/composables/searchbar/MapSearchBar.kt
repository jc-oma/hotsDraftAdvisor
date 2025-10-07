package com.example.hotsdraftadviser.composables.searchbar

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.hotsdraftadviser.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapSearchBar(
    modifier: Modifier,
    searchQuery: String,
    updateMapsSearchQuery: (String) -> Unit,
    label: String
) {
    TextField(
        modifier = modifier.padding(start = 8.dp, end = 8.dp)
            .border(1.dp, Color.Gray, RoundedCornerShape(50)),
        value = searchQuery,
        onValueChange = { newText ->
            updateMapsSearchQuery(newText)
        },
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        label = { Row {
            Icon(Icons.Default.Search, contentDescription = "Search")
            Text(text = " $label")
        } },
        shape = RoundedCornerShape(50),
        trailingIcon = {
            if (searchQuery.isNotEmpty()) {
                Icon(
                    Icons.Filled.Clear,
                    contentDescription = "Clear text",
                    modifier = Modifier.clickable {
                        updateMapsSearchQuery("")
                    }
                )
            }
        }
    )
}

@Preview
@Composable
private fun ChampSearchBarPreview() {
    Row {
        MapSearchBar(
            searchQuery = "Alterac",
            updateMapsSearchQuery = {},
            modifier = Modifier.weight(1f),
            label = stringResource(R.string.main_activity_maps_suchen)
        )
    }
}