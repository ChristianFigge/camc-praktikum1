package com.example.camc_praktikum1.ui.screens.components.sensorpanel

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.camc_praktikum1.viewmodel.SensorViewModel
import com.example.camc_praktikum1.viewmodel.utils.SensorTypeData


@Composable
fun SensorPanel(
    sensorType: SensorTypeData,
    viewModel: SensorViewModel,
    modifier: Modifier = Modifier
) {
    val ctx = LocalContext.current
    val sensorIsRunning by remember { sensorType.isRunning }
    val sensorHasData = sensorType.listener!!.value.hasData

    Column(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        PrintSensorData(sensorType.label, sensorType.dataString)

        //Spacer(Modifier.height(20.dp))
        Text("")

        SampleSpeedControl(sensorType)

        Spacer(Modifier.height(20.dp))

        //+++++++++++++++++++++ BUTTONS +++++++++++++++++++++++++++
        // START/STOP
         /*
        Button(
            onClick = {
                if (!sensorIsRunning) {
                    viewModel.startSensor(sensorType)
                } else {
                    viewModel.stopSensor(sensorType)
                }
            },
            content = {
                if (!sensorIsRunning) Text("Start")
                else Text("Stop")
            },
        )
        Spacer(Modifier.height(10.dp))

          */



        Row() {
            // DELETE
            Button(
                onClick = {
                    viewModel.clearRecordingData(sensorType, ctx)
                },
                content = { Text("Löschen") },
                enabled = !sensorIsRunning && sensorHasData,
            )
            Spacer(Modifier.width(10.dp))
            // SAVE
            Button(
                onClick = {
                    viewModel.saveRecordingInStorage(sensorType, ctx)
                },
                content = { Text("Speichern") },
                enabled = !sensorIsRunning && sensorHasData,
            )
            Spacer(Modifier.width(20.dp))
            Button(
                onClick = {
                    if (!sensorIsRunning) {
                        viewModel.startSensor(sensorType)
                    } else {
                        viewModel.stopSensor(sensorType)
                    }
                },
                content = {
                    if (!sensorIsRunning) Text("Start")
                    else Text("Stop")
                },
            )
        }
        /*
        // SLIDER suckt plötzlich
        Row(
            verticalAlignment = Alignment.Bottom,
            modifier = modifier
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "Sample speed: ${sensorDelays[sliderState.roundToInt()].name}",
                    modifier = Modifier.padding(bottom = 40.dp)
                )
                Slider(
                    value = sliderState,
                    onValueChange = { sliderState = it },
                    valueRange = 0f..(sensorDelays.size - 1).toFloat(),
                    steps = 2, // 4 steps in total (excludes smallest and greatest value for whatever reason)
                    enabled = !sensorIsRunning,
                    modifier = Modifier.width(200.dp)
                )
            }
            Button(
                onClick = {
                    if (!sensorIsRunning) {
                        viewModel.registerSensorListener(
                            sensorType,
                            sensorDelays[sliderState.roundToInt()]
                        )
                    } else {
                        viewModel.unregisterSensorListener(sensorType)
                    }
                },
                content = {
                    if (!sensorIsRunning) Text("Start")
                    else Text("Stop")
                },
                modifier = Modifier.padding(bottom = 20.dp, start = 20.dp),
            )
        }

         */
    }
}