package com.example.camc_praktikum1.viewmodel

import android.content.Context
import android.hardware.SensorManager
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.camc_praktikum1.viewmodel.utils.SensorListener
import com.example.camc_praktikum1.viewmodel.utils.SensorTypeData


class SensorViewModel private constructor(
    ctx: Context,
):
    ViewModel()
{
    companion object { // static in kotlin
        @Volatile
        private var instance: SensorViewModel? = null

        fun getInstance(ctx: Context) =
            instance ?: synchronized(this) {
                SensorViewModel(ctx).also { instance = it }
            }
    }

    private var sensorManager = ctx.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    init {
        SensorTypeData.entries.forEach { type ->
            type.listener = mutableStateOf(SensorListener(type))
        }
    }

    /***  ------------------------ START Sensor Steuerung ------------------------------ ***/
    /**
     * Registriert den Default-Sensor eines Sensor-Typs beim globalen SensorManager.
     * @param sensorType integer Identifier für den Sensor Typ (z.B. Sensor.TYPE_LIGHT)
     */
    fun startSensor(sensorType : SensorTypeData) {
        val delay = sensorType.delayType.value
        sensorManager.registerListener(
            sensorType.listener!!.value,
            sensorManager.getDefaultSensor(sensorType.type_id),
            delay.id,
            //freqUs
        )
        sensorType.isRunning.value = true

        Log.d(
            "SensorControlDbg",
            "Started ${sensorType.name.uppercase()} with ${delay.name.uppercase()} delay"
        )
    }

    /**
     * Meldet Default-Sensor beim globalen SensorManager ab.
     * @param sensorType integer Identifier für den Sensor Typ (z.B. Sensor.TYPE_LIGHT)
     */
    fun stopSensor(sensorType : SensorTypeData) {
        sensorManager.unregisterListener(sensorType.listener!!.value)
        sensorType.isRunning.value = false
    }

    fun clearData(sensorType: SensorTypeData) {
        sensorType.listener!!.value.clearData()
    }

}