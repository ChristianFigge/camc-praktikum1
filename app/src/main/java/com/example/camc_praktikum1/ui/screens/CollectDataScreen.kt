package com.example.camc_praktikum1.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.camc_praktikum1.viewmodel.CollectDataViewModel
import com.example.camc_praktikum1.viewmodel.utils.SensorTypeData
import com.example.camc_praktikum1.viewmodel.utils.TransportMode

@Composable
fun CollectDataScreen(
    modifier: Modifier = Modifier,
) {

    val ctx = LocalContext.current
    val viewModel by remember { mutableStateOf(CollectDataViewModel.getInstance(ctx)) }

    // helper fns ( werden geinlined) TODO als mutableState im viewmodel
    fun anySensorHasData(): Boolean {
        SensorTypeData.entries.forEach {
            if (it.listener!!.value.hasData)
                return true
        }
        return false
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        Spacer(Modifier.height(30.dp))

        // ++++++++++++++++++++ TRANSPORT MODE SELECT ++++++++++++++++++++++
        Text(
            "Aktueller Bewegungsmodus",
            fontWeight=FontWeight.Bold
        )
        Spacer(Modifier.height(10.dp))
        Column(
            //modifier = Modifier.padding(start = 30.dp)
        ) {
            //items(TransportMode.entries.toList()) {
            TransportMode.entries.forEach {
                Box(modifier = Modifier
                    .height(40.dp).padding(end=10.dp)
                    .clickable { viewModel.transportMode = it }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = viewModel.transportMode == it,
                            onClick = { viewModel.transportMode = it }
                        )
                        Text("${it.name}    ")
                    }
                }
            }

        }
        Spacer(Modifier.height(30.dp))


        // +++++++++++++++++++++++++ BUTTONS ++++++++++++++++++++++++++++++
        Row() {
            Button(
                content = { Text("Daten\nlöschen", textAlign = TextAlign.Center) },
                onClick = { viewModel.clearAllRecordingData(ctx) },
                enabled = viewModel.noSensorIsRunning && anySensorHasData(),
            )
            Spacer(Modifier.width(10.dp))
            Button(
                content = { Text("Daten\nspeichern", textAlign = TextAlign.Center) },
                onClick = { viewModel.saveAllRecordingsInStorage(ctx) },
                enabled = viewModel.noSensorIsRunning && anySensorHasData(),
            )
            Spacer(Modifier.width(20.dp))
            Button(
                content = {
                    val btnText =
                        if (viewModel.noSensorIsRunning) "Daten\nsammeln"
                        else "Sammeln\nstoppen"
                    Text(btnText, textAlign = TextAlign.Center)
                },
                onClick = {
                    if (viewModel.noSensorIsRunning)
                        viewModel.startSelectedSensors()
                    else
                        viewModel.stopAllRunningSensors()
                },
                enabled = !(viewModel.noSensorIsRunning && (viewModel.noSensorIsSelected || anySensorHasData()))
            )
        }
        Spacer(Modifier.height(20.dp))

        HorizontalDivider()

        // ++++++++++++++++++++++++ SENSOR SELECTION ++++++++++++++++++++++++
        Spacer(Modifier.height(30.dp))
        Text(
            "Sensorauswahl",
            //style = MaterialTheme.typography.titleMedium,
            fontWeight=FontWeight.Bold
        )
        Spacer(Modifier.height(10.dp))
        Column() {
            //items(SensorTypeData.entries.toList()) {
            SensorTypeData.entries.forEach {
                Box(
                    modifier = Modifier.height(40.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Checkbox(
                            enabled = viewModel.noSensorIsRunning,
                            checked = it.isSelected.value,
                            onCheckedChange = { isChecked ->
                                viewModel.selectSensor(it, isChecked)
                            },
                        )
                        Text(it.label)
                    }
                }
            }
        }
        Spacer(Modifier.height(40.dp))
    }
}