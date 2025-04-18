package com.example.camc_praktikum1.data.models

import kotlinx.serialization.Serializable

@Serializable
data class DataCollectionMeta(
    val sensorName: String,
    val fileName: String,
    val createdAt: String,
    val size: Int,
    val durationMs: Long,
)
