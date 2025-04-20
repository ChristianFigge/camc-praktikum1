package com.example.camc_praktikum1.viewmodel.utils

import java.util.UUID

fun getRecordingSessionId(): String {
    return UUID.randomUUID().toString().split("-").last()
}