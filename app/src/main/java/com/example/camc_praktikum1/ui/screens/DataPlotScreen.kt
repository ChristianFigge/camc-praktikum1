package com.example.camc_praktikum1.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.camc_praktikum1.viewmodel.DataViewModel


@Composable
fun DataPlotScreen(
    modifier: Modifier = Modifier,
) {
    val ctx = LocalContext.current
    val viewModel by remember { mutableStateOf(DataViewModel.getInstance(ctx)) }

    val metaData = viewModel.selectedMetaData  //.collectAsState()
    val data = viewModel.selectedData

    if(data.isEmpty()) {
        Text("Wähle einen anderen Datensatz!")
        return
    }

    Column(){
        Text("TODO plot this data: \n\n$data")
    }

}