package com.example.camc_praktikum1.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
        modifier = Modifier.verticalScroll(rememberScrollState()),
    ) {
        Text(
            "Context-Aware\nund Mobile Computing",
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium,
            style= MaterialTheme.typography.titleLarge
        )
        Text("Sommersemester 2025",
            textAlign = TextAlign.Center,
            //style= MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.height(30.dp))

        Text("Entwickelt von:",
            fontWeight = FontWeight.Bold,
            //textDecoration = TextDecoration.Underline,
            //style= MaterialTheme.typography.titleMedium
        )
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
            fontWeight = FontWeight.Bold,
            //textDecoration = TextDecoration.Underline,
            //style= MaterialTheme.typography.titleMedium
        )
        Text(
            "Android Studio\n" +
            "Kotlin mit Jetpack Compose",
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(40.dp))

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