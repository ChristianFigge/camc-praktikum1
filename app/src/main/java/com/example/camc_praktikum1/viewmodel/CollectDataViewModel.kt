package com.example.camc_praktikum1.viewmodel

import android.content.Context
import android.hardware.SensorManager
import androidx.compose.runtime.mutableStateOf
import com.example.camc_praktikum1.viewmodel.utils.SensorTypeData
import com.example.camc_praktikum1.viewmodel.utils.TransportMode
import android.util.Log
import androidx.compose.runtime.mutableIntStateOf

class CollectDataViewModel private constructor (
    sensorManager: SensorManager,
    writeDataStrings: Boolean = false,
):
    SensorViewModel(sensorManager, writeDataStrings)
{
    companion object { // static in kotlin
        @Volatile
        private var instance: CollectDataViewModel? = null

        fun getInstance(ctx: Context) =
            instance ?: synchronized(this) {
                val sensorMan = ctx.getSystemService(Context.SENSOR_SERVICE) as SensorManager
                CollectDataViewModel(sensorMan).also { instance = it }
            }
    }

    private val _transportMode = mutableStateOf<TransportMode>(TransportMode.Stillstand)
    var transportMode: TransportMode
        get() = _transportMode.value
        set(mode: TransportMode) {
            Log.d("CollectDataDbg", "Selected transport mode ${mode.name.uppercase()}")
            _transportMode.value = mode
        }

    private val sensorSelectionFlags = mutableIntStateOf(0)
    val noSensorIsSelected: Boolean
        get() = sensorSelectionFlags.intValue == 0

    init {
        SensorTypeData.entries.forEach {
            if(it.isSelected.value) {
                setSensorSelectionFlag(it)
            }
        }
    }

    fun startSelectedSensors() {
        SensorTypeData.entries.forEach {
            if(it.isSelected.value)
                startSensor(it)
        }
    }

    fun stopAllRunningSensors() {
        SensorTypeData.entries.forEach {
            if(it.isRunning.value)
                stopSensor(it)
        }
    }


    private fun setSensorSelectionFlag(sensorType: SensorTypeData) {
        sensorSelectionFlags.intValue =
            sensorSelectionFlags.intValue or (1 shl sensorType.ordinal)
    }

    private fun unsetSensorSelectionFlag(sensorType: SensorTypeData) {
        sensorSelectionFlags.intValue =
            sensorSelectionFlags.intValue and (1 shl sensorType.ordinal).inv()
    }

    fun selectSensor(sensorType: SensorTypeData, isSelected: Boolean) {
        sensorType.isSelected.value = isSelected

        // set selection flags for ui
        if(isSelected)
            setSensorSelectionFlag(sensorType)
        else
            unsetSensorSelectionFlag(sensorType)
    }
}