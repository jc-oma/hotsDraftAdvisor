package com.example.hotsdraftadviser.composables.segmentedButton

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.hotsdraftadviser.R
import com.example.hotsdraftadviser.SortState
import com.example.hotsdraftadviser.getColorByHexString

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
    val screenBackgroundColor = "150e35ff"
    val composeScreenBackgroundColor = getColorByHexString(screenBackgroundColor)

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
                label = { Text(list[i]) }
            )
        }
    }
}

@Preview
@Composable
private fun SegmentedPreview() {
    SegmentedButtonToOrderChamplistComposable(
        {}, SortState.OWNPOINTS,
        onButtonClick = {}
    )
}