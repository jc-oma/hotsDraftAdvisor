package com.example.hotsdraftadviser.composables.segmentedButton

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.hotsdraftadviser.SortState

@Composable
fun SegmentedButtonToOrderChamplistComposable(setSortState: (SortState) -> Unit, sortState: SortState) {
    var selectedIndex by remember { mutableIntStateOf(sortState.ordinal) }
    var list = listOf<String>("Best Pick", "Best Ban", "Name")
    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
        for ((i, state) in SortState.entries.withIndex()) {
            SegmentedButton(
                colors = SegmentedButtonDefaults.colors(
                    activeBorderColor = Color.White,
                    inactiveBorderColor = Color.White,
                    activeContentColor = Color.White),
                shape = SegmentedButtonDefaults.itemShape(
                    index = i,
                    count = SortState.entries.size
                ),
                onClick = {
                    selectedIndex = i
                    setSortState(state)
                },
                selected = i == selectedIndex,
                label = { Text(list[i], color = Color.White) }
            )
        }
    }
}

@Preview
@Composable
private fun SegmentedPreview() {
    SegmentedButtonToOrderChamplistComposable({}, SortState.OWNPOINTS)
}