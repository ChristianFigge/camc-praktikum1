package com.example.camc_praktikum1.ui.screens.components.sensorpanel

import android.util.Log
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.camc_praktikum1.viewmodel.utils.SensorDelay
import com.example.camc_praktikum1.viewmodel.utils.SensorTypeData


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SampleSpeedControl(
    sensorType: SensorTypeData,
    modifier: Modifier = Modifier,
) {
    val sensorIsRunning by remember { sensorType.isRunning }
    var sensorDelayType by remember { sensorType.delayType }
    var menuIsExpanded by remember { mutableStateOf(false) }

    fun getMenuItemLabel(delay: SensorDelay): String {
        return delay.name + " (${delay.freqMs} ms)"
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("Sample Delay:")
        Spacer(Modifier.width(10.dp))
        ExposedDropdownMenuBox(
            expanded = menuIsExpanded,
            onExpandedChange = { menuIsExpanded = !menuIsExpanded },
            modifier = Modifier.width(200.dp),
        ) {
            TextField(
                modifier = Modifier.menuAnchor(), // idgaf
                value = getMenuItemLabel(sensorDelayType),
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuIsExpanded) },
                enabled = !sensorIsRunning,
            )
            ExposedDropdownMenu(
                expanded = menuIsExpanded,
                onDismissRequest = { menuIsExpanded = false },
            ) {
                SensorDelay.entries.forEach { delayType ->
                    DropdownMenuItem(
                        text = { Text( getMenuItemLabel(delayType) ) },
                        onClick = {
                            sensorDelayType = delayType
                            menuIsExpanded = false
                            Log.d(
                                "SensorControlDBG",
                                "Selected Delay Type ${delayType.name.uppercase()} for ${sensorType.name.uppercase()}"
                            )
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        enabled = !sensorIsRunning
                    )
                }
            }
        }
    }
}