package com.example.camc_praktikum1.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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

    Column() {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(20.dp))
            Row() {
                Button(
                    content = { Text("Clear All") },
                    modifier = Modifier.padding(horizontal = 10.dp),
                    onClick = {
                        viewModel.clearAllData(ctx)
                    }
                )
                Button(
                    content = { Text("Save All") },
                    modifier = Modifier.padding(horizontal = 10.dp),
                    onClick = { viewModel.writeAllDataToStorage(ctx) }
                )
                Button(
                    content = { Text("Start All") },
                    modifier = Modifier.padding(horizontal = 10.dp),
                    onClick = { }
                )
            }
            Spacer(Modifier.height(20.dp))
            HorizontalDivider(thickness = 2.dp)
        }

        Column(
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
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
}