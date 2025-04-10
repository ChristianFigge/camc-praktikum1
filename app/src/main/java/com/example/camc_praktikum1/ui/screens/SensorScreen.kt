package com.example.camc_praktikum1.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.camc_praktikum1.viewmodel.SensorViewModel

@Composable
fun SensorScreen(
    modifier: Modifier = Modifier,
    //viewModel: SensorViewModel = viewModel(),
) {
    val viewModel by remember { mutableStateOf(SensorViewModel.getInstance())}

    Column() {
        Text(
            text = "Hello, Sensor Screen!",
            modifier = modifier
        )

        TextField(
            value = "",
            onValueChange = { newText ->
                viewModel.setTestString(newText)
            }
        )

        Text(
            text = "viewmodel cache:\n${viewModel.getTestString()}",
            modifier = modifier
        )
    }
}