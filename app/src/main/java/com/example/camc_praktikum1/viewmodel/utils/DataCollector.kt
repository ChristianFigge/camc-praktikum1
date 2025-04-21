package com.example.camc_praktikum1.viewmodel.utils

import android.content.Context
import android.hardware.SensorEvent
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.camc_praktikum1.data.InternalStorage.Companion.readRecordingIndex
import com.example.camc_praktikum1.data.InternalStorage.Companion.saveDataAsJsonFile
import com.example.camc_praktikum1.data.InternalStorage.Companion.updateRecordingIndex
import com.example.camc_praktikum1.data.models.RecordingMetaData
import com.example.camc_praktikum1.data.models.SensorEventData
import java.io.FileNotFoundException
import java.io.IOException


/**
 * Saves SensorEventData in a list.
 */
open class DataCollector(private val sensorType: SensorTypeData) {
    private val sensorName = sensorType.name

    private var _data = mutableListOf<SensorEventData>()
    val data: List<SensorEventData>
        get() = _data.toList() // shallow copy

    private val _hasData: MutableState<Boolean> = mutableStateOf(_data.isNotEmpty())
    val hasData : Boolean
        get() = _hasData.value

    fun collectDatum(sensorEvent: SensorEvent) {
        val newItem = SensorEventData(
            values = sensorEvent.values.clone(),
            timestamp = sensorEvent.timestamp, //System.currentTimeMillis()
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
    fun saveDataInStorage(
        ctx: Context,
        pTimeMs: Long? = null,
        pSessionId: String? = null,
        cleanUp: Boolean = true,
    ) {
        if(_data.isEmpty())
            return // todo throw smth?

        val timeMs: Long = pTimeMs ?: System.currentTimeMillis()
        val sessionId: String = pSessionId ?: getRecordingSessionId()
        val fileName: String = sensorName + "_${sessionId}.json"
        val data = _data.toList()

        saveDataAsJsonFile(data, fileName, ctx)

        // update data index
        val metaData = RecordingMetaData(
            sensorName = sensorName,
            fileName = fileName,
            createdAt = timeMs, //sdf.format(Date(timeMs)),
            size = data.size,
            durationMs = data.last().timestamp - data.first().timestamp,
            sessionId = sessionId,
        )
        val colIdx = readRecordingIndex(ctx)
        colIdx.add(metaData)
        updateRecordingIndex(colIdx, ctx)

        if(cleanUp) this.clearData()
    }
}