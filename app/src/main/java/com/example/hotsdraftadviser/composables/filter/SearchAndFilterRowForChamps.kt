package com.example.hotsdraftadviser.composables.filter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.hotsdraftadviser.R
import com.example.hotsdraftadviser.RoleEnum
import com.example.hotsdraftadviser.composables.composabaleUtilitis.getResponsiveFontSize
import com.example.hotsdraftadviser.composables.searchbar.ChampSearchBar

@Composable
fun SearchAndFilterRowForChamps(
    searchQueryOwnTChamps: String,
    roleFilter: List<RoleEnum>,
    favFilter: Boolean,
    setRoleFilter: (RoleEnum?) -> Unit,
    updateChampSearchQuery: (String) -> Unit,
    toggleFavFilter: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom
        ) {
            ChampSearchBar(
                modifier = Modifier
                    .padding(start = 8.dp, end = 8.dp)
                    .weight(2f),
                searchQueryChamps = searchQueryOwnTChamps,
                setRoleFilter = {it -> setRoleFilter(it)},
                updateChampSearchQuery = {it -> updateChampSearchQuery(it)}
            )
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.BottomStart
            ) {
                FilterChip(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, end = 8.dp),
                    leadingIcon = {
                        Icon(
                            imageVector = (Icons.Filled.Favorite),
                            contentDescription = "Heart",
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    },
                    selected = favFilter,
                    onClick = { toggleFavFilter() },
                    label = {
                        Text(
                            stringResource(R.string.filter_favorite),
                            fontSize = getResponsiveFontSize(),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                )
            }
        }
        Column(
            verticalArrangement = Arrangement.Top
        ) {
            val imagePadding = 8.dp
            val responsiveFontSize = getResponsiveFontSize()

            Row(modifier = Modifier.padding(top = imagePadding)) {
                FilterChip(
                    modifier = Modifier
                        .weight(0.5f)
                        .padding(start = imagePadding, end = imagePadding),
                    leadingIcon = {
                        Icon(
                            painterResource(id = R.drawable.tank),
                            contentDescription = "Description of your image",
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    },
                    selected = roleFilter.contains(RoleEnum.tank),
                    onClick = { setRoleFilter(RoleEnum.tank) },
                    label = {
                        Text(
                            stringResource(R.string.main_acitivity_tank),
                            fontSize = responsiveFontSize,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                )
                FilterChip(
                    modifier = Modifier
                        .weight(0.5f)
                        .padding(start = imagePadding, end = imagePadding),
                    leadingIcon = {
                        Icon(
                            painterResource(id = R.drawable.ranged),
                            contentDescription = "Description of your image",
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    },
                    selected = roleFilter.contains(RoleEnum.ranged),
                    onClick = { setRoleFilter(RoleEnum.ranged) },
                    label = {
                        Text(
                            stringResource(R.string.main_acitivity_ranged),
                            fontSize = responsiveFontSize,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                )
                FilterChip(
                    modifier = Modifier
                        .weight(0.5f)
                        .padding(start = imagePadding, end = imagePadding),
                    leadingIcon = {
                        Icon(
                            painterResource(id = R.drawable.melee),
                            contentDescription = "Description of your image",
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    },
                    selected = roleFilter.contains(RoleEnum.melee),
                    onClick = { setRoleFilter(RoleEnum.melee) },
                    label = {
                        Text(
                            stringResource(R.string.main_acitivity_melee),
                            fontSize = responsiveFontSize,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                )
            }
            Row {
                FilterChip(
                    modifier = Modifier
                        .weight(0.5f)
                        .padding(start = imagePadding, end = imagePadding),
                    leadingIcon = {
                        Icon(
                            painterResource(id = R.drawable.heiler),
                            contentDescription = "Description of your image",
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    },
                    selected = roleFilter.contains(RoleEnum.heal),
                    onClick = { setRoleFilter(RoleEnum.heal) },
                    label = {
                        Text(
                            stringResource(R.string.main_acitivity_heal),
                            fontSize = responsiveFontSize,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                )
                FilterChip(
                    modifier = Modifier
                        .weight(0.5f)
                        .padding(start = imagePadding, end = imagePadding),
                    leadingIcon = {
                        Icon(
                            painterResource(id = R.drawable.bruiser),
                            contentDescription = "Description of your image",
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    },
                    selected = roleFilter.contains(RoleEnum.bruiser),
                    onClick = { setRoleFilter(RoleEnum.bruiser) },
                    label = {
                        Text(
                            stringResource(R.string.main_acitivity_bruiser),
                            fontSize = responsiveFontSize,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                )
                FilterChip(
                    modifier = Modifier
                        .weight(0.5f)
                        .padding(start = imagePadding, end = imagePadding),
                    leadingIcon = {
                        Icon(
                            painterResource(id = R.drawable.support),
                            contentDescription = "Description of your image",
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    },
                    selected = roleFilter.contains(RoleEnum.support),
                    onClick = { setRoleFilter(RoleEnum.support) },
                    label = {
                        Text(
                            stringResource(R.string.main_acitivity_support),
                            fontSize = responsiveFontSize,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                )
            }
        }
    }
}

@Preview
@Composable
private fun SearchAndFilterRowForChampsPreview() {
    SearchAndFilterRowForChamps(
        searchQueryOwnTChamps = "Hammer",
        roleFilter = listOf(RoleEnum.tank, RoleEnum.melee),
        favFilter = false,
        setRoleFilter = {},
        updateChampSearchQuery = {},
        toggleFavFilter = {}
    )
}