package com.example.hotsdraftadviser.composables.menus

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.hotsdraftadviser.R

@Preview
@Composable
fun DisclaimerComposable(onClose: () -> Unit = {}) {
    Column (
        modifier = Modifier
            .padding(16.dp)
            .background(Color.White)
            .padding(10.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(modifier = Modifier.padding(18.dp),
            text = stringResource(id = R.string.disclaimer))
        Button(
            onClick = { onClose() },
        ) {
            Text(text = "CLOSE", style = MaterialTheme.typography.labelLarge)
        }
    }
}