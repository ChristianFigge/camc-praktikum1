package com.example.camc_praktikum1.ui.screens.components.sensorpanel

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration

@Composable
fun PrintSensorData(
    label: String,
    dataString: MutableState<String>?,
    modifier: Modifier = Modifier
) {
    Text(
        label,
        textDecoration = TextDecoration.Underline,
    )
    Text(
        "${dataString?.value}",
        textAlign = TextAlign.Center,
    )

    /*
    Text(
        text = buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    textDecoration = TextDecoration.Underline
                )
            ) {
                append("$label:\n")
            }
            append("${dataString?.value}")
        },
        textAlign = TextAlign.Center,
        modifier = modifier,
    )

     */
}