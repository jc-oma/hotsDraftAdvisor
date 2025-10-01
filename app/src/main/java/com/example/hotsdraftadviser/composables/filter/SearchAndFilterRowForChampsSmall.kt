package com.example.hotsdraftadviser.composables.filter

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hotsdraftadviser.R
import com.example.hotsdraftadviser.RoleEnum

@Composable
fun SearchAndFilterRowForChampsSmall(
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
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painterResource(id = R.drawable.tank),
                                contentDescription = "Description of your image",
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )
                        }
                    },
                    selected = roleFilter.contains(RoleEnum.tank),
                    onClick = { setRoleFilter(RoleEnum.tank) },
                    label = {}
                )
                FilterChip(
                    modifier = Modifier
                        .weight(0.5f)
                        .padding(start = imagePadding, end = imagePadding),
                    leadingIcon = {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painterResource(id = R.drawable.ranged),
                                contentDescription = "Description of your image",
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )
                        }
                    },
                    selected = roleFilter.contains(RoleEnum.ranged),
                    onClick = { setRoleFilter(RoleEnum.ranged) },
                    label = {}
                )
                FilterChip(
                    modifier = Modifier
                        .weight(0.5f)
                        .padding(start = imagePadding, end = imagePadding),
                    leadingIcon = {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painterResource(id = R.drawable.melee),
                                contentDescription = "Description of your image",
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )
                        }
                    },
                    selected = roleFilter.contains(RoleEnum.melee),
                    onClick = { setRoleFilter(RoleEnum.melee) },
                    label = {}
                )
                FilterChip(
                    modifier = Modifier
                        .weight(0.5f)
                        .padding(start = imagePadding, end = imagePadding),
                    leadingIcon = {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painterResource(id = R.drawable.heiler),
                                contentDescription = "Description of your image",
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )
                        }
                    },
                    selected = roleFilter.contains(RoleEnum.heal),
                    onClick = { setRoleFilter(RoleEnum.heal) },
                    label = {}
                )
                FilterChip(
                    modifier = Modifier
                        .weight(0.5f)
                        .padding(start = imagePadding, end = imagePadding),
                    leadingIcon = {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painterResource(id = R.drawable.bruiser),
                                contentDescription = "Description of your image",
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )
                        }
                    },
                    selected = roleFilter.contains(RoleEnum.bruiser),
                    onClick = { setRoleFilter(RoleEnum.bruiser) },
                    label = {}
                )
                FilterChip(
                    modifier = Modifier
                        .weight(0.5f)
                        .padding(start = imagePadding, end = imagePadding),
                    leadingIcon = {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painterResource(id = R.drawable.support),
                                contentDescription = "Description of your image",
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )
                        }
                    },
                    selected = roleFilter.contains(RoleEnum.support),
                    onClick = { setRoleFilter(RoleEnum.support) },
                    label = {}
                )
                FilterChip(
                    modifier = Modifier
                        .weight(0.5f)
                        .padding(start = 8.dp, end = 8.dp),
                    leadingIcon = {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = (Icons.Filled.Favorite),
                                contentDescription = "Heart",
                                modifier = Modifier.size(FilterChipDefaults.IconSize))
                        }
                    },
                    selected = favFilter,
                    onClick = { toggleFavFilter() },
                    label = {}
                )
            }
        }
    }
}

@Composable
private fun getResponsiveFontSize(): TextUnit {
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp.dp

    return if (screenWidthDp < 360.dp) {
        12.sp
    } else if (screenWidthDp < 480.dp) {
        14.sp
    } else {
        16.sp // Ihre aktuelle fontSize
    }
}

@Preview
@Composable
private fun SearchAndFilterRowForChampsSmallPreview() {
    SearchAndFilterRowForChampsSmall(
        searchQueryOwnTChamps = "Hammer",
        roleFilter = listOf(RoleEnum.tank, RoleEnum.melee),
        favFilter = false,
        setRoleFilter = {},
        updateChampSearchQuery = {},
        toggleFavFilter = {}
    )
}