package com.example.camc_praktikum1.data.models

import kotlinx.serialization.Serializable

@Serializable
data class SensorEventData(
    val values: FloatArray,
    val timestampNs: Long,
    val transportMode: String = ""
)
