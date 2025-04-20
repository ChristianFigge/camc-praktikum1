package com.example.camc_praktikum1.viewmodel.utils

import android.content.Context
import android.hardware.SensorEvent
import android.icu.text.SimpleDateFormat
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.camc_praktikum1.data.InternalStorage.Companion.readCollectionIndex
import com.example.camc_praktikum1.data.InternalStorage.Companion.saveDataAsJsonFile
import com.example.camc_praktikum1.data.InternalStorage.Companion.updateCollectionIndex
import com.example.camc_praktikum1.data.models.DataCollectionMeta
import com.example.camc_praktikum1.data.models.SensorEventData
import java.io.FileNotFoundException
import java.io.IOException
import java.util.Date
import java.util.Locale


/**
 * Saves SensorEventData in a list.
 */
open class DataCollector(private val sensorType: SensorTypeData) {
    private val sensorName = sensorType.name
    private val sdf = SimpleDateFormat("dd.MM.yy HH:mm:ss", Locale.getDefault())

    private var _data = mutableListOf<SensorEventData>()
    val data: List<SensorEventData>
        get() = _data.toList() // shallow copy

    private val _hasData: MutableState<Boolean> = mutableStateOf(_data.isNotEmpty())
    val hasData : Boolean
        get() = _hasData.value

    fun collectDatum(sensorEvent: SensorEvent) {
        val newItem = SensorEventData(
            values = sensorEvent.values,
            timestamp = System.currentTimeMillis()
        )
        _data.add(newItem)

        if(!hasData)
            _hasData.value = true
    }

    fun clearData() {
        _data.clear()
        _hasData.value = false
        sensorType.dataString.value = DATASTRING_INIT
    }

    @Throws(FileNotFoundException::class, IOException::class)
    fun saveDataInStorage(ctx: Context, cleanUp: Boolean = true, pTimeMs: Long? = null) {
        if(_data.isEmpty())
            return // todo throw smth?

        val timeMs: Long = pTimeMs ?: System.currentTimeMillis()
        val fileName: String = sensorName + "_${timeMs}.json"
        val data = _data.toList()

        saveDataAsJsonFile(data, fileName, ctx)

        // update data index
        val metaData = DataCollectionMeta(
            sensorName = sensorName,
            fileName = fileName,
            createdAt = sdf.format(Date(timeMs)),
            size = data.size,
            durationMs = data.last().timestamp - data.first().timestamp,
        )
        val colIdx = readCollectionIndex(ctx)
        colIdx.add(metaData)
        updateCollectionIndex(colIdx, ctx)

        if(cleanUp) this.clearData()
    }
}