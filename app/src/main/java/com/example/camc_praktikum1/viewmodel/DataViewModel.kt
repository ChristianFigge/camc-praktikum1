package com.example.camc_praktikum1.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.camc_praktikum1.data.models.DataCollectionMeta
import com.example.camc_praktikum1.data.models.SensorEventData
import com.example.camc_praktikum1.viewmodel.utils.DataCollector
import com.example.camc_praktikum1.data.StorageIO

class DataViewModel private constructor(
    ctx: Context,
):
    ViewModel()
{
    companion object { // static in kotlin
        @Volatile
        private var instance: DataViewModel? = null

        fun getInstance(ctx: Context) =
            instance ?: synchronized(this) {
                DataViewModel(ctx).also { instance = it }
            }
    }

    //private val _selectedRecordings = MutableStateFlow<List<SensorEventData>>(emptyList())
    //var selectedRecordings: StateFlow<List<SensorEventData>> = _selectedRecordings.asStateFlow()

    private val _selectedMetaData = mutableStateOf<DataCollectionMeta?>(null)
    val selectedMetaData: MutableState<DataCollectionMeta?>
        get() = _selectedMetaData

    private val _data = mutableStateOf(listOf<SensorEventData>())
    val selectedData: List<SensorEventData>
        get() = _data.value

    fun readCollectionIndex(ctx: Context): MutableList<DataCollectionMeta> {
        return DataCollector.readCollectionIndex(ctx)
    }

    fun selectData(metaData: DataCollectionMeta, ctx: Context) {
        _selectedMetaData.value = metaData
        loadSelectedData(ctx)?.let { _data.value = it }
    }

    fun deselectData() {
        _selectedMetaData.value = null
        _data.value = listOf<SensorEventData>()
    }

    fun deleteData(
        metaData: DataCollectionMeta,
        ctx: Context,
        successMsg: String? = "Eintrag gelöscht"
    ) {
        try {
            StorageIO.deleteRecording(metaData, ctx)
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

    fun loadSelectedData(ctx: Context): List<SensorEventData>? {
        _selectedMetaData.value?.let {
            try {
                return StorageIO.readSensorRecording(it.fileName, ctx)
            } catch(ex: Exception) {
                ex.printStackTrace()
            }
        }
        return null
    }
}