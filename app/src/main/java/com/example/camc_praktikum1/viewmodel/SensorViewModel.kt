package com.example.camc_praktikum1.viewmodel

import android.content.Context
import android.hardware.SensorManager
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.camc_praktikum1.viewmodel.utils.SensorListener
import com.example.camc_praktikum1.viewmodel.utils.SensorTypeData
import com.example.camc_praktikum1.viewmodel.utils.getRecordingSessionId


open class SensorViewModel internal constructor(
    private val sensorManager: SensorManager,
):
    ViewModel()
{
    companion object { // static in kotlin
        @Volatile
        private var instance: SensorViewModel? = null

        fun getInstance(ctx: Context) =
            instance ?: synchronized(this) {
                val sensorMan = ctx.getSystemService(Context.SENSOR_SERVICE) as SensorManager
                SensorViewModel(sensorMan).also { instance = it }
            }
    }

    private val sensorSelectionFlags = mutableIntStateOf(0)
    val noSensorIsRunning: Boolean
        get() = sensorSelectionFlags.intValue == 0

    init {
        SensorTypeData.entries.forEach { type ->
            type.listener = mutableStateOf(SensorListener(type))

            Log.d(
                "SensorControlDbg",
                "Initialized ${type.name.uppercase()} Listener")
        }
    }

    /***  ------------------------ START Listener Steuerung ------------------------------ ***/
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

        // set running flag
        sensorSelectionFlags.intValue = sensorSelectionFlags.intValue or (1 shl sensorType.ordinal)

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
        sensorType.dataString.value = "\n(Angehalten)\n"

        // unset running flag
        sensorSelectionFlags.intValue = sensorSelectionFlags.intValue and (1 shl sensorType.ordinal).inv()

        Log.d(
            "SensorControlDbg",
            "Stopped ${sensorType.name.uppercase()}"
        )
    }

    fun startAllSensors() {
        SensorTypeData.entries.forEach { startSensor(it) }
    }

    fun stopAllSensors() {
        SensorTypeData.entries.forEach { stopSensor(it) }
    }

    fun clearRecordingData(sensorType: SensorTypeData, ctx: Context, toastMsg: String? = "Daten gelöscht") {
        sensorType.listener!!.value.clearData()

        toastMsg?.let {
            Toast.makeText(ctx, "${sensorType.label} $toastMsg", Toast.LENGTH_SHORT).show()
        }
    }

    fun clearAllRecordingData(ctx: Context, toastMsg: String? = "Alle Daten gelöscht") {
        SensorTypeData.entries.forEach {
            it.listener!!.value.clearData()
        }

        toastMsg?.let {
            Toast.makeText(ctx, it, Toast.LENGTH_SHORT).show()
        }
    }
    /***  ------------------------ ENDE Listener Steuerung ------------------------------ ***/


    /*** ----------------------------- START I/O ---------------------------- ***/
    fun saveRecordingInStorage(
        sensorType: SensorTypeData,
        ctx: Context,
        successToastMsg : String? = "Daten gespeichert",
        cleanUp: Boolean = true
    ) {
        try {
            sensorType.listener!!.value.saveDataInStorage(ctx, cleanUp = cleanUp)
        } catch(e: Exception) {
            Toast.makeText(ctx, "Fehler beim Speichern :(", Toast.LENGTH_LONG).show()
            e.printStackTrace()
            return
        }

        successToastMsg?.let {
            Toast.makeText(ctx, it, Toast.LENGTH_SHORT).show()
        }
    }

    fun saveAllRecordingsInStorage(
        ctx : Context,
        successToastMsg : String? = "Alle Daten gespeichert",
        cleanUp: Boolean = true
    ) {
        val timeMs = System.currentTimeMillis()
        val sessionId = getRecordingSessionId()
        try {
            SensorTypeData.entries.forEach {
                it.listener!!.value.saveDataInStorage(ctx, timeMs, sessionId, cleanUp)
            }
        } catch(e: Exception) {
            Toast.makeText(ctx, "Fehler beim Speichern :(", Toast.LENGTH_LONG).show()
            e.printStackTrace()
            return
        }

        successToastMsg?.let {
            Toast.makeText(ctx, it, Toast.LENGTH_SHORT).show()
        }
    }
    /*** ------------------------------ ENDE I/O ---------------------------- ***/

}