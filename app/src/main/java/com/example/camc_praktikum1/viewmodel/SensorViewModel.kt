package com.example.camc_praktikum1.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import com.example.camc_praktikum1.viewmodel.utils.SensorListener
import com.example.camc_praktikum1.viewmodel.utils.SensorTypeData


class SensorViewModel private constructor(
    ctx: Context,
): ViewModel() {
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
     * @param sampleFrequencyMs gewünschte Sample Geschwindigkeit des Sensors in Millisekunden
     */
    fun registerSensorListener(sensorType : SensorTypeData, sampleFrequencyMs : Int) {
        val freqUs = sampleFrequencyMs * 1000
        sensorManager.registerListener(
            sensorType.listener!!.value,
            sensorManager.getDefaultSensor(sensorType.type_id),
            freqUs,
            freqUs
        )
    }

    /**
     * Meldet Default-Sensor beim globalen SensorManager ab.
     * @param sensorType integer Identifier für den Sensor Typ (z.B. Sensor.TYPE_LIGHT)
     */
    fun unregisterSensorListener(sensorType : SensorTypeData) {
        sensorManager.unregisterListener(sensorType.listener!!.value)
    }

    // persistence test
    val test = mutableStateOf("")

    fun getTestString(): String {
        return test.value
    }

    fun setTestString(strInput: String) {
        // SENSOR TEST TODO delete
        if(strInput.contains("q")) {
            unregisterSensorListener(SensorTypeData.Accelerometer)
            print("SENSOR LSITENER STOPPED")
            print("SENSORTYPE:" + SensorTypeData.Accelerometer.listener!!.value)
        } else if(strInput.contains("s")) {
            print("SENSORTYPE:" + SensorTypeData.Accelerometer.listener!!.value)
            registerSensorListener(SensorTypeData.Accelerometer, 200)
        }
        else {
            print("wtf")
        }
        test.value += strInput
    }
}