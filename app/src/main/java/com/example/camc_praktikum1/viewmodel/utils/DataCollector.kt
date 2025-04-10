package com.example.camc_praktikum1.viewmodel.utils

import android.content.Context
import android.hardware.SensorEvent
import com.example.camc_praktikum1.data.models.SensorEventData
import kotlinx.serialization.json.Json
import java.io.FileNotFoundException
import java.io.IOException


/**
 * Saves Event data in a hashMap with timestamps as key.
 */
open class DataCollector(open val sensorName: String) {
    private var currentData = mutableListOf<SensorEventData>() //LinkedHashMap<Long, DoubleArray>()

    fun collectDatum(sensorEvent: SensorEvent) {
        currentData.add(
            SensorEventData(
                values = sensorEvent.values,
                timestamp = System.currentTimeMillis()
            )
        )
    }
    /*
    fun collectDatum(sensorEvent: SensorEvent) {
        val dblValues = DoubleArray(sensorEvent.values.size) { i ->
            sensorEvent.values[i].toDouble()
        }
        this.currentData[sensorEvent.timestamp] = dblValues
    }

    fun collectDatum(loc: Location) {
        this.currentData[loc.time] = doubleArrayOf(loc.latitude, loc.longitude /* altitude, accuracy?... */)
    }

     */

    fun getData(): List<SensorEventData> {//LinkedHashMap<Long, DoubleArray> {
        return this.currentData
    }

    fun getDataAsJson(): String {
        return Json.encodeToString(this.currentData)
    }

    fun clearData() {
        this.currentData.clear()
    }

    @Throws(FileNotFoundException::class, IOException::class)
    fun writeJsonToStorage(context: Context, cleanUp: Boolean = true) {
        //val sdf = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault())

        val jsonData : String = this.getDataAsJson()
        context.openFileOutput("${this.sensorName}.json", Context.MODE_PRIVATE).use {
            it.write(jsonData.toByteArray())
        }

        if(cleanUp) this.clearData()
    }

    @Throws(FileNotFoundException::class)
    fun readJsonFromStorage(context: Context) : String {
        return context.openFileInput("${this.sensorName}.json").bufferedReader().readText()
    }
}