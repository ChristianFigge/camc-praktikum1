package com.example.camc_praktikum1.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.camc_praktikum1.ui.screens.components.DataPlotPanel
import com.example.camc_praktikum1.viewmodel.DataViewModel


@Composable
fun DataPlotScreen(

) {
    val ctx = LocalContext.current
    val viewModel by remember { mutableStateOf(DataViewModel.getInstance()) }
    val metaData by remember { viewModel.selectedMetaData }

    Column(
        modifier=Modifier.verticalScroll(rememberScrollState()),
    ){
        DataPlotPanel(
            metaData = metaData,
            viewModel = viewModel,
            ctx = ctx,
        )
    }

}