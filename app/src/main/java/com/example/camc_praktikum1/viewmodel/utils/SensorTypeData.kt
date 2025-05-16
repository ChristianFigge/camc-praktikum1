package com.example.camc_praktikum1.viewmodel.utils

import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf


enum class SensorDelay(
    val id: Int,
    val freqMs: Long,
    val sliderValue: Float
) {
    Normal(
        id= SensorManager.SENSOR_DELAY_NORMAL,
        freqMs=200,
        sliderValue=3f
    ),
    UI(
        id= SensorManager.SENSOR_DELAY_UI,
        freqMs=60,
        sliderValue=2f
    ),
    Game(
        id= SensorManager.SENSOR_DELAY_GAME,
        freqMs=20,
        sliderValue=1f
    ),
    Fastest(
        id= SensorManager.SENSOR_DELAY_FASTEST,
        freqMs=0,
        sliderValue=0f
    )
}

const val DATASTRING_INIT = "\n(Noch keine Daten)\n"

enum class SensorTypeData(
    val type_id: Int,
    val label: String,
    val dataString: MutableState<String> = mutableStateOf(DATASTRING_INIT),
    var listener: MutableState<SensorListener>? = null,
    var isRunning: MutableState<Boolean> = mutableStateOf(false),
    var isSelected: MutableState<Boolean> = mutableStateOf(false),
    var delayType: MutableState<SensorDelay> = mutableStateOf(SensorDelay.Normal),
) {
    Accelerometer(
        type_id = Sensor.TYPE_ACCELEROMETER,
        label = "Accelerometer",
        isSelected = mutableStateOf(true),
    ),
    Gravity(
        type_id = Sensor.TYPE_GRAVITY,
        label = "Gravitation",
    ),
    LinearAccel(
        type_id = Sensor.TYPE_LINEAR_ACCELERATION,
        label = "Lineare Beschleunigung",
    ),
    Gyroscope(
        type_id = Sensor.TYPE_GYROSCOPE,
        label = "Gyroskop",
        isSelected = mutableStateOf(true),
    ),
    MagneticField(
        type_id = Sensor.TYPE_MAGNETIC_FIELD,
        label = "Magnetfeld"
    ),
    Rotation(
        type_id = Sensor.TYPE_ROTATION_VECTOR,
        label = "Geräte-Rotation"
    ),
    Orientation(
        type_id = Sensor.TYPE_ORIENTATION,
        label = "Geräte-Orientierung (ALT)"
    ),
}