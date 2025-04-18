package com.example.camc_praktikum1.viewmodel

import android.content.Context
import android.hardware.SensorManager
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.camc_praktikum1.viewmodel.utils.SensorListener
import com.example.camc_praktikum1.viewmodel.utils.SensorTypeData
import java.io.FileNotFoundException


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

        Log.d(
            "SensorControlDbg",
            "Stopped ${sensorType.name.uppercase()}"
        )
    }

    fun clearData(sensorType: SensorTypeData, ctx: Context) {
        sensorType.listener!!.value.clearData()
        Toast.makeText(ctx, "${sensorType.label} Daten gelöscht", Toast.LENGTH_SHORT).show()
    }
    /***  ------------------------ ENDE Listener Steuerung ------------------------------ ***/


    /*** ----------------------------- START I/O ---------------------------- ***/
    fun writeDataToStorage(
        sensorType: SensorTypeData,
        ctx: Context,
        successMsg : String? = "Daten gespeichert",
        cleanUp: Boolean = true
    ) {
        try {
            sensorType.listener!!.value.writeJsonToStorage(ctx, cleanUp)
        } catch(e: Exception) {
            Toast.makeText(ctx, "Data saving failed!", Toast.LENGTH_LONG).show()
            e.printStackTrace()
            return
        }

        successMsg?.let {
            Toast.makeText(ctx, it, Toast.LENGTH_SHORT).show()
        }
    }

    fun writeAllDataToStorage(
        ctx : Context,
        successMsg : String? = "Daten gespeichert",
        cleanUp: Boolean = true
    ) {
        SensorTypeData.entries.forEach {
            writeDataToStorage(it, ctx, null, cleanUp)
        }

        successMsg?.let {
            Toast.makeText(ctx, it, Toast.LENGTH_SHORT).show()
        }
    }

    fun readAllDataFromStorage(ctx : Context) : String {
        val strOut = StringBuilder()
        SensorTypeData.entries.forEach {
            strOut.append("${it.name} data:\n\n")
            try {
                strOut.append(it.listener!!.value.readJsonFromStorage(ctx) + "\n\n")
            } catch(e: FileNotFoundException) {
                strOut.append("File not found.\n\n")
            }
        }
        return strOut.toString()
    }

    fun clearAllData(ctx : Context) {
        SensorTypeData.entries.forEach {
            it.listener!!.value.clearData()
        }
        //writeAllDataToStorage(ctx,"Data cleared") // clear json files
    }
    /*
    fun readCollectionIndex(ctx: Context): MutableList<DataCollectionMeta> {
        return DataCollector.readCollectionIndex(ctx)
    }*/
    /*** ------------------------------ ENDE I/O ---------------------------- ***/

}