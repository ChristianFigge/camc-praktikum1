package com.example.camc_praktikum1.ui.screens

import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.camc_praktikum1.data.models.RecordingMetaData
import com.example.camc_praktikum1.viewmodel.DataViewModel
import java.util.Date
import java.util.Locale


@Composable
fun DataIndexScreen(
    onShowPlotClick: () -> Unit,
    onGotoSensorsClick: () -> Unit,
    onExportClick: (ByteArray, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val ctx = LocalContext.current
    val viewModel by remember { mutableStateOf(DataViewModel.getInstance()) }
    var selectedMetaData by remember { viewModel.selectedMetaData }
    val sdf = SimpleDateFormat("dd.MM.yy HH:mm:ss", Locale.getDefault())

    // Helper fncs:
    fun getRecordingIndex(): List<RecordingMetaData>? {
        return viewModel.readRecordingIndex(ctx, orderByTimeDescending = true)
    }
    var collectionIndex by remember { mutableStateOf(getRecordingIndex()) }


    Column(
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(20.dp))
            Row() {
                Button(
                    content = { Text("Löschen") },
                    enabled = selectedMetaData != null,
                    modifier = Modifier.padding(horizontal = 10.dp),
                    onClick = {
                        viewModel.deleteRecordingFromStorage(selectedMetaData!!, ctx)
                        // refresh view
                        collectionIndex = getRecordingIndex()
                    }
                )
                Button(
                    content = { Text("Graph anzeigen") },
                    enabled = selectedMetaData != null,
                    modifier = Modifier.padding(horizontal = 10.dp),
                    onClick = { onShowPlotClick() }
                )
            }
            //Spacer(Modifier.height(10.dp))
            Row() {
                Button(
                    content = { Text("Session exportieren") },
                    enabled = selectedMetaData != null,
                    modifier = Modifier.padding(horizontal = 10.dp),
                    onClick = {
                        val exportData: Pair<ByteArray, String>? = viewModel.getSessionExportData(ctx)
                        exportData?.let {
                            val bytes = it.first
                            val fileName = it.second
                            onExportClick(bytes, fileName)
                        }
                    }
                )

            }
            Spacer(Modifier.height(20.dp))
            HorizontalDivider(thickness = 2.dp)
        }

        if(collectionIndex.isNullOrEmpty()) {
            NoDataYetMessage { onGotoSensorsClick() }
            return
        }

        Spacer(Modifier.height(20.dp))

        LazyColumn(
            modifier=Modifier.padding(start=30.dp)
        ) {
            collectionIndex?.let { index ->
                items(index) {
                    Row() {
                        RadioButton(
                            selected = selectedMetaData == it,
                            onClick = {
                                viewModel.selectData(it)
                            }
                        )
                        Column() {
                            Text(
                                it.sensorName + ", " + sdf.format(Date(it.createdAt)),
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "(${it.size} Events in ${it.durationMs / 1000} s)\n" +
                                        "SessionId: " + it.sessionId,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                }
            }
        }
    }
}

@Composable
fun NoDataYetMessage(onGotoSensorsClick: () -> Unit,) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            "Noch keine Daten vorhanden\n\n",
            style = MaterialTheme.typography.titleLarge,
        )
        Text(
            "Starte einen Sensor\nund speichere die Daten!\n\n",
            textAlign = TextAlign.Center,
        )
        Button(
            content= { Text("Zu den Sensoren") },
            onClick= { onGotoSensorsClick() },
        )
    }
}