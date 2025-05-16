package com.example.camc_praktikum1.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf

@Composable
fun CollectDataScreen(
    modifier: Modifier = Modifier,
) {

    val selectedSensorsFlags by remember { mutableIntStateOf(0) }

    Text("Hi :)")
}