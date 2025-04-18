package com.example.camc_praktikum1.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.unit.dp
import com.example.camc_praktikum1.data.models.DataCollectionMeta
import com.example.camc_praktikum1.viewmodel.DataViewModel

@Composable
fun DataIndexScreen(
    onShowPlotClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val ctx = LocalContext.current
    val viewModel by remember { mutableStateOf(DataViewModel.getInstance(ctx)) }
    var strFileContents by remember { mutableStateOf("") }
    var selectedMetaData by remember { viewModel.selectedMetaData }

    // Helper fncs:
    fun getCollectionIndex(): List<DataCollectionMeta> {
        return viewModel.readCollectionIndex(ctx).asReversed().toList()
    }

    var collectionIndex by remember { mutableStateOf(getCollectionIndex()) }

    strFileContents = "$collectionIndex"

    /*** I/O Controls ***/
    Column(
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(20.dp))
            Row() {
                Button(
                    content = { Text("Delete") },
                    enabled = selectedMetaData != null,
                    modifier = Modifier.padding(horizontal = 10.dp),
                    onClick = {
                        viewModel.deleteData(selectedMetaData!!, ctx)
                        // refresh view
                        collectionIndex = getCollectionIndex()
                    }
                )
                Button(
                    content = { Text("Show plot") },
                    enabled = selectedMetaData != null,
                    modifier = Modifier.padding(horizontal = 10.dp),
                    onClick = { onShowPlotClick() }
                )
            }
            Spacer(Modifier.height(20.dp))
            HorizontalDivider(thickness = 2.dp)
        }

        Spacer(Modifier.height(20.dp))

        LazyColumn(
            modifier=Modifier.padding(start=30.dp)
        ) {
            items(collectionIndex) {
                Row() {
                    RadioButton(
                        selected = selectedMetaData == it,
                        onClick = {
                            viewModel.selectData(it, ctx)
                        }
                    )
                    Column() {
                        Text(
                            it.sensorName + ", " + it.createdAt,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "(${it.size} Events in ${it.durationMs} ms)",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }


            }
        }
    }
}