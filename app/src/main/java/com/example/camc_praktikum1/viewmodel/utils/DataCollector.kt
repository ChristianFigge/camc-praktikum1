package com.example.camc_praktikum1.viewmodel.utils

import android.content.Context
import android.hardware.SensorEvent
import android.icu.text.SimpleDateFormat
import com.example.camc_praktikum1.data.models.DataCollectionMeta
import com.example.camc_praktikum1.data.models.SensorEventData
import kotlinx.serialization.json.Json
import java.io.FileNotFoundException
import java.io.IOException
import java.util.Date
import java.util.Locale


/**
 * Saves Event data in a hashMap with timestamps as key.
 */

val INDEX_FILENAME = "index.json"

open class DataCollector(open val sensorName: String) {
    companion object { // static in kotlin
        private val INDEX_FILENAME = "data_index.json"

        //@Volatile
        //private var collectionIndex = mutableListOf<DataCollectionMeta>()

        fun readCollectionIndex(ctx: Context): MutableList<DataCollectionMeta> {
            var colIdx = mutableListOf<DataCollectionMeta>()
            try {
                val fileContent: String = ctx.openFileInput(INDEX_FILENAME).bufferedReader().readText()
                colIdx = Json.decodeFromString(fileContent)
            } catch(fileEx: FileNotFoundException) {
                //pass
            }
            return colIdx
        }

        private fun updateCollectionIndex(ctx: Context, newCollectionIndex: MutableList<DataCollectionMeta>) {
            val jsonString = Json.encodeToString(newCollectionIndex)
            ctx.openFileOutput(INDEX_FILENAME, Context.MODE_PRIVATE).use {
                it.write(jsonString.toByteArray())
            }
        }

        @Throws(FileNotFoundException::class)
        fun readJsonFromStorage(fileName: String, ctx: Context) : String {
            return ctx.openFileInput(fileName).bufferedReader().readText()
        }
    }

    private val sdf = SimpleDateFormat("dd.MM.yy HH:mm:ss", Locale.getDefault())


    private var currentData = mutableListOf<SensorEventData>()
    fun collectDatum(sensorEvent: SensorEvent) {
        currentData.add(
            SensorEventData(
                values = sensorEvent.values,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    fun getData(): List<SensorEventData> {
        return this.currentData
    }

    /*
    private var currentData = LinkedHashMap<Long, FloatArray>()
    fun collectDatum(sensorEvent: SensorEvent) {
        this.currentData[sensorEvent.timestamp] = sensorEvent.values
    }

    fun getData(): LinkedHashMap<Long, FloatArray> {
        return this.currentData
    }

     */

    fun getDataAsJson(): String {
        return Json.encodeToString(this.currentData)
    }

    fun clearData() {
        this.currentData.clear()
    }

    @Throws(FileNotFoundException::class, IOException::class)
    fun writeJsonToStorage(ctx: Context, cleanUp: Boolean = true) {
        if(currentData.isEmpty())
            return

        val timeMs: Long = System.currentTimeMillis()
        val fileName: String = this.sensorName + "_${timeMs}.json"

        val metaData = DataCollectionMeta(
            sensorName = this.sensorName,
            fileName = fileName,
            createdAt = sdf.format(Date(timeMs)),
            size = currentData.size,
            durationMs = currentData.last().timestamp - currentData.first().timestamp,
        )

        val colIdx = readCollectionIndex(ctx)
        colIdx.add(metaData)
        //Log.i("asd", "${colIdx}")

        val jsonData : String = this.getDataAsJson()
        ctx.openFileOutput(fileName, Context.MODE_PRIVATE).use {
            it.write(jsonData.toByteArray())
        }

        updateCollectionIndex(ctx, colIdx)

        if(cleanUp) this.clearData()
    }

    @Throws(FileNotFoundException::class)
    fun readJsonFromStorage(ctx: Context) : String {
        return ctx.openFileInput("${this.sensorName}.json").bufferedReader().readText()
    }
}