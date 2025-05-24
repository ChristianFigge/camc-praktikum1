package com.example.camc_praktikum1.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * enum values that represent the screens in the app
 */
enum class AppScreenData(
    val title: String,
    val icon: ImageVector,
    val showInDrawer: Boolean = true,
) {
    Home(
        title = "Home",
        icon = Icons.Default.Home,
    ),
    /*
    AllSensors(
        title = "Alle Sensoren",
        icon = Icons.Default.Search,
    ),
    */
    Data(
        title = "Daten",
        icon = Icons.Default.DateRange,
    ),
    DataPlot(
        title = "Daten Plot",
        icon = Icons.Default.Info,
        showInDrawer = false,
    ),
    CollectData(
        title = "Daten sammeln",
        icon = Icons.Default.Face,
    )
}