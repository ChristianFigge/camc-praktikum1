package com.example.camc_praktikum1.data.models

import kotlinx.serialization.Serializable

@Serializable
data class RecordingMetaData(
    val sensorName: String,
    val fileName: String,
    val createdAt: Long,
    val size: Int,
    val durationMs: Long,
    val sessionId: String,
)
