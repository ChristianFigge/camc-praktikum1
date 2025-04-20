package com.example.camc_praktikum1.ui.screens.components

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.camc_praktikum1.data.models.RecordingMetaData
import com.example.camc_praktikum1.viewmodel.DataViewModel
import kotlin.collections.isNullOrEmpty

@Composable
fun DataPlotPanel(
    metaData: RecordingMetaData?,
    viewModel: DataViewModel,
    ctx: Context,
) {
    var data = viewModel.loadRecordingFromFile(metaData, ctx)

    if(data.isNullOrEmpty()) {
        Text("Wähle einen anderen Datensatz!")
        return
    }

    Column(){
        Text("TODO plot this data: \n\n$data")
    }

}
