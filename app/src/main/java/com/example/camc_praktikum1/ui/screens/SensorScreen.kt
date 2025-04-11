package com.example.camc_praktikum1.ui.screens

import android.hardware.Sensor
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.camc_praktikum1.viewmodel.SensorViewModel
import com.example.camc_praktikum1.viewmodel.utils.SensorTypeData


@Composable
fun SensorScreen(
    modifier: Modifier = Modifier,
    //viewModel: SensorViewModel = viewModel(),
) {
    val ctx = LocalContext.current
    val viewModel by remember { mutableStateOf(SensorViewModel.getInstance(ctx))}


    // SENSOR TEST todo delete
    var testRunning by remember { mutableStateOf(false) }
    if(!testRunning) {
        viewModel.registerSensorListener(SensorTypeData.Accelerometer, 200)
        testRunning = true
    }

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