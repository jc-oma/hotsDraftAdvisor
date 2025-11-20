package com.jcdevelopment.hotsdraftadviser.composables.segmentedButton

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.jcdevelopment.hotsdraftadviser.SortState
import com.jcdevelopment.hotsdraftadviser.R

@Composable
fun SegmentedButtonToOrderChamplistComposable(
    setSortState: (SortState) -> Unit,
    sortState: SortState,
    onButtonClick: () -> Unit
) {
    var selectedIndex by remember { mutableIntStateOf(sortState.ordinal) }
    val list = listOf<String>(stringResource(R.string.segment_button_best_pick),
        stringResource(R.string.segment_button_best_ban),
        stringResource(R.string.segment_button_name)
    )

    SingleChoiceSegmentedButtonRow(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        for ((i, state) in SortState.entries.withIndex()) {
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(
                    index = i,
                    count = SortState.entries.size
                ),
                onClick = {
                    selectedIndex = i
                    setSortState(state)
                    onButtonClick()
                },
                selected = i == selectedIndex,
                label = { Text(text =list[i], maxLines = 1, overflow = TextOverflow.StartEllipsis) }
            )
        }
    }
}

@Preview
@Composable
private fun SegmentedPreview() {
    SegmentedButtonToOrderChamplistComposable(
        setSortState = {},
        sortState = SortState.OWNPOINTS,
        onButtonClick = {}
    )
}