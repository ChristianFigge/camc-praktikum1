package com.example.camc_praktikum1.viewmodel.utils

import android.hardware.Sensor
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

enum class SensorTypeData(
    val type_id: Int,
    val label: String,
    var listener: MutableState<SensorListener>? = null,
    val dataString: MutableState<String> = mutableStateOf(""),

    ) {
    Accelerometer(
        type_id = Sensor.TYPE_ACCELEROMETER,
        label = "Accelerometer",
    ),
    LinearAccel(
        type_id = Sensor.TYPE_LINEAR_ACCELERATION,
        label = "Lineare Beschleunigung",
    ),
    Gyroscope(
        type_id = Sensor.TYPE_GYROSCOPE,
        label = "Gyroskop"
    ),
    MagneticField(
        type_id = Sensor.TYPE_MAGNETIC_FIELD,
        label = "Magnetfeld"
    )
}