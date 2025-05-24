package com.example.camc_praktikum1.data.models

import kotlinx.serialization.Serializable

@Serializable
data class SensorEventData(
    /** Sensor data values */
    val values: FloatArray,
    /** Timestamp of the Event in Milliseconds */
    val timestampMillis: Long,
    /** Transportation mode (or SYNC)*/
    val mode: String = ""
)
