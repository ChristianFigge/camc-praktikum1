package com.example.camc_praktikum1.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    onGotoSensorsClick: () -> Unit,
    onGotoDataClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            "Context-Aware\nund Mobile Computing",
            textAlign = TextAlign.Center,
            style= MaterialTheme.typography.titleLarge)
        Text("Sommersemester 2025",
            textAlign = TextAlign.Center,
            style= MaterialTheme.typography.titleSmall)

        Spacer(Modifier.height(30.dp))

        Text("Entwickelt von:",
            textDecoration = TextDecoration.Underline,
            style= MaterialTheme.typography.titleMedium)
        Text(
            "Theo Andrup\n" +
            "Christian Figge\n" +
            "Marc Morzinietz\n" +
            "Tim Stenger",
            textAlign = TextAlign.Center
        )

        //Spacer(modifier = Modifier.height(10.dp))
        Text("")

        Text("Tools:",
            textDecoration = TextDecoration.Underline,
            style= MaterialTheme.typography.titleMedium)
        Text(
            "Kotlin und JetPack Compose",
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(30.dp))

        Button(
            content = { Text("Zu den Sensoren") },
            onClick = { onGotoSensorsClick() }
        )
        Spacer(Modifier.height(10.dp))
        Button(
            content = { Text("Zu den Daten") },
            onClick = { onGotoDataClick() }
        )



    }
}