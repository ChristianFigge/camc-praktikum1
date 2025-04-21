package com.example.camc_praktikum1.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.camc_praktikum1.data.models.RecordingMetaData
import com.example.camc_praktikum1.data.models.SensorEventData
import com.example.camc_praktikum1.data.InternalStorage

class DataViewModel private constructor(
):
    ViewModel()
{
    companion object { // static in kotlin
        @Volatile
        private var instance: DataViewModel? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                DataViewModel().also { instance = it }
            }
    }
    
    private val _selectedMetaData = mutableStateOf<RecordingMetaData?>(null)
    val selectedMetaData: MutableState<RecordingMetaData?>
        get() = _selectedMetaData


    fun readRecordingIndex(
        ctx: Context,
        orderByTimeDescending: Boolean = false,
    ): List<RecordingMetaData>? {
        try {
            val index = InternalStorage.readRecordingIndex(ctx).toList()
            if(orderByTimeDescending)
                return index.asReversed()
            return index
        } catch(ex: Exception) {
            ex.printStackTrace()
            Toast.makeText(ctx, "Failed to read index data", Toast.LENGTH_LONG).show()
            return null
        }
    }

    fun selectData(metaData: RecordingMetaData) {
        _selectedMetaData.value = metaData
    }

    fun deselectData() {
        _selectedMetaData.value = null
    }

    fun deleteRecordingFromStorage(
        metaData: RecordingMetaData,
        ctx: Context,
        successMsg: String? = "Eintrag gelöscht"
    ) {
        try {
            InternalStorage.deleteRecordingFromStorage(metaData, ctx)
        } catch(ex: Exception) {
            ex.printStackTrace()
            Toast.makeText(ctx, "Failed to delete file", Toast.LENGTH_LONG).show()
            return
        }

        if(metaData == _selectedMetaData.value) {
            deselectData()
        }

        successMsg?.let {
            Toast.makeText(ctx, it, Toast.LENGTH_SHORT).show()
        }
    }

    fun loadRecordingFromFile(metaData: RecordingMetaData?, ctx: Context): List<SensorEventData>? {
        metaData?.let {
            try {
                return InternalStorage.loadRecordingFromFile(it.fileName, ctx)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
        return null
    }
}