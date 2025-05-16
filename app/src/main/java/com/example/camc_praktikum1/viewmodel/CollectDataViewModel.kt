package com.example.camc_praktikum1.viewmodel

import android.content.Context
import android.hardware.SensorManager
import com.example.camc_praktikum1.viewmodel.utils.SensorTypeData
import com.example.camc_praktikum1.viewmodel.utils.TransportMode
import android.util.Log
import androidx.compose.runtime.mutableIntStateOf
import com.example.camc_praktikum1.viewmodel.utils.DataCollector

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

    //private val _transportMode = mutableStateOf(DataCollector.transportMode)//mutableStateOf<TransportMode>(TransportMode.Stillstand)
    var transportMode: TransportMode
        get() = DataCollector.transportMode //_transportMode.value
        set(mode) {
            DataCollector.transportMode = mode
            Log.d("CollectDataDbg", "Selected transport mode ${mode.name.uppercase()}")
        }

    private val sensorSelectionFlags = mutableIntStateOf(0)
    val noSensorIsSelected: Boolean
        get() = sensorSelectionFlags.intValue == 0

    init {
        // init sensor selection flags for ui
        SensorTypeData.entries.forEach {
            if(it.isSelected.value) {
                setSensorSelectionFlag(it)
            }
        }
    }

    fun startSelectedSensors() {
        val nowMillis = System.currentTimeMillis()
        SensorTypeData.entries.forEach {
            if(it.isSelected.value) {
                it.listener!!.value.saveSyncDatum(nowMillis)
                startSensor(it)
            }
        }
    }

    fun stopAllRunningSensors() {
        val nowMillis = System.currentTimeMillis()
        SensorTypeData.entries.forEach {
            if(it.isRunning.value) {
                stopSensor(it)
                it.listener!!.value.saveSyncDatum(nowMillis)
            }
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