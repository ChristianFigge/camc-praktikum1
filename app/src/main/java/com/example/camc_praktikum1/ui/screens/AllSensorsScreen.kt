package com.example.camc_praktikum1.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.camc_praktikum1.ui.screens.components.sensorpanel.SensorPanel
import com.example.camc_praktikum1.viewmodel.SensorViewModel
import com.example.camc_praktikum1.viewmodel.utils.SensorTypeData


@Composable
fun AllSensorsScreen(
    modifier: Modifier = Modifier,
) {
    val ctx = LocalContext.current
    val viewModel by remember { mutableStateOf(SensorViewModel.getInstance(ctx))}

    // helper fns ( werden geinlined) TODO als mutableState im viewmodel
    fun anySensorHasData(): Boolean {
        SensorTypeData.entries.forEach {
            if(it.listener!!.value.hasData)
                return true
        }
        return false
    }

    Column() {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(20.dp))
            Row() {
                Button(
                    content = { Text("Alle Daten\nlöschen", textAlign= TextAlign.Center) },
                    onClick = { viewModel.clearAllSensorData(ctx) },
                    enabled = viewModel.noSensorIsRunning && anySensorHasData(),
                )
                Spacer(Modifier.width(10.dp))
                Button(
                    content = { Text("Alle Daten\nspeichern", textAlign= TextAlign.Center) },
                    onClick = { viewModel.saveAllSensorDataInStorage(ctx) },
                    enabled = viewModel.noSensorIsRunning && anySensorHasData(),
                )
                Spacer(Modifier.width(20.dp))
                Button(
                    content = {
                        val btnText =
                            if(viewModel.noSensorIsRunning) "Sensoren\nstarten"
                            else "Sensoren\nstoppen"
                        Text(btnText, textAlign=TextAlign.Center)
                    },
                    onClick = {
                        if(viewModel.noSensorIsRunning)
                            viewModel.startAllSensors()
                        else
                            viewModel.stopAllSensors()
                    },
                )
            }
            Spacer(Modifier.height(20.dp))
            HorizontalDivider(thickness = 2.dp)
        }

        LazyColumn() {
            items(SensorTypeData.entries.toList()) {
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
}