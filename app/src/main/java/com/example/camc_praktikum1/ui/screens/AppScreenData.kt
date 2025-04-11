package com.example.camc_praktikum1.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * enum values that represent the screens in the app
 */
enum class AppScreenData(
    val title: String,
    val icon: ImageVector,
) {
    Home(
        title = "Home",
        icon = Icons.Default.Home,
    ),
    AllSensors(
        title = "Sensoren",
        icon = Icons.Default.Search,
    ),
}