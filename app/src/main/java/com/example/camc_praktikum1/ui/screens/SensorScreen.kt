package com.example.camc_praktikum1.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.camc_praktikum1.ui.screens.components.sensorpanel.SensorPanel
import com.example.camc_praktikum1.viewmodel.SensorViewModel
import com.example.camc_praktikum1.viewmodel.utils.SensorTypeData


@Composable
fun SensorScreen(
    modifier: Modifier = Modifier,
    //viewModel: SensorViewModel = viewModel(),
) {
    val ctx = LocalContext.current
    val viewModel by remember { mutableStateOf(SensorViewModel.getInstance(ctx))}

    /*
    // SENSOR TEST todo delete
    var testRunning by remember { mutableStateOf(false) }
    if(!testRunning) {
        viewModel.registerSensorListener(SensorTypeData.Accelerometer, 200)
        testRunning = true
    }

     */

    // TEST for Accelerometer
    // TODO iterate over SensorTypes
    val accel = SensorTypeData.Accelerometer

    Column() {
        SensorTypeData.entries.forEach {
            Spacer(Modifier.height(30.dp))
            SensorPanel(
                sensorType = it,
                viewModel = viewModel,
            )
            Spacer(Modifier.height(30.dp))
            HorizontalDivider()
        }
    }
}