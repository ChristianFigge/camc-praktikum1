package com.example.camc_praktikum1.viewmodel.utils

import android.content.Context
import android.hardware.SensorEvent
import android.icu.text.SimpleDateFormat
import com.example.camc_praktikum1.data.models.DataCollectionMeta
import com.example.camc_praktikum1.data.models.SensorEventData
import com.example.camc_praktikum1.data.InternalStorage.Companion.readCollectionIndex
import com.example.camc_praktikum1.data.InternalStorage.Companion.saveDataAsJsonFile
import com.example.camc_praktikum1.data.InternalStorage.Companion.updateCollectionIndex
import java.io.FileNotFoundException
import java.io.IOException
import java.util.Date
import java.util.Locale


/**
 * Saves SensorEventData in a list.
 */
open class DataCollector(open val sensorName: String) {
    private val sdf = SimpleDateFormat("dd.MM.yy HH:mm:ss", Locale.getDefault())

    private var currentData = mutableListOf<SensorEventData>()
    val data: List<SensorEventData>
        get() = currentData.toList()

    fun collectDatum(sensorEvent: SensorEvent) {
        val newItem = SensorEventData(
            values = sensorEvent.values,
            timestamp = System.currentTimeMillis()
        )
        currentData.add(newItem)
    }

    fun clearData() {
        currentData.clear()
    }

    @Throws(FileNotFoundException::class, IOException::class)
    fun saveDataInStorage(ctx: Context, cleanUp: Boolean = true) {
        if(currentData.isEmpty())
            return // todo throw smth?

        val timeMs: Long = System.currentTimeMillis()
        val fileName: String = sensorName + "_${timeMs}.json"

        saveDataAsJsonFile(data, fileName, ctx)

        // update data index
        val metaData = DataCollectionMeta(
            sensorName = sensorName,
            fileName = fileName,
            createdAt = sdf.format(Date(timeMs)),
            size = currentData.size,
            durationMs = currentData.last().timestamp - currentData.first().timestamp,
        )
        val colIdx = readCollectionIndex(ctx)
        colIdx.add(metaData)
        updateCollectionIndex(colIdx, ctx)

        if(cleanUp) this.clearData()
    }
}