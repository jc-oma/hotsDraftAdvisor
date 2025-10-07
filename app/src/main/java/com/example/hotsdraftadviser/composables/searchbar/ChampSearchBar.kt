package com.example.hotsdraftadviser.composables.searchbar

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.hotsdraftadviser.R
import com.example.hotsdraftadviser.RoleEnum
import com.example.hotsdraftadviser.composables.composabaleUtilitis.getResponsiveFontSize

@Composable
fun ChampSearchBar(
    modifier: Modifier,
    searchQueryChamps: String,
    setRoleFilter: (RoleEnum?) -> Unit,
    updateChampSearchQuery: (String) -> Unit
) {
    TextField(
        modifier = modifier
            .padding(start = 8.dp, end = 8.dp)
            .border(1.dp, Color.Gray, RoundedCornerShape(50)),
        value = searchQueryChamps,
        onValueChange = { newText: String ->
            setRoleFilter(null)
            updateChampSearchQuery(newText)
        },
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        label = {
            Row {
                Icon(Icons.Default.Search, contentDescription = "Search")
                Text(
                    text = " " + stringResource(R.string.main_activity_champs_suchen),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        shape = RoundedCornerShape(50),
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
        searchQueryChamps = "Tyrael",
        setRoleFilter = {},
        updateChampSearchQuery = {}
    )
}