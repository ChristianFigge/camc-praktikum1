package com.example.camc_praktikum1.data.models

import kotlinx.serialization.Serializable

@Serializable
data class ExportSessionData(
    val sessionId: String,
    val createdAt: Long,
    val durationMs: Long,
    val sensorTypes: List<String>,
    val sensorRecordings: List<List<SensorEventData>>,
)