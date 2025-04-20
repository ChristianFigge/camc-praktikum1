package com.example.camc_praktikum1.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.camc_praktikum1.data.models.DataCollectionMeta
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
    
    private val _selectedMetaData = mutableStateOf<DataCollectionMeta?>(null)
    val selectedMetaData: MutableState<DataCollectionMeta?>
        get() = _selectedMetaData


    fun readCollectionIndex(ctx: Context): MutableList<DataCollectionMeta> {
        return InternalStorage.readCollectionIndex(ctx)
    }

    fun selectData(metaData: DataCollectionMeta) {
        _selectedMetaData.value = metaData
    }

    fun deselectData() {
        _selectedMetaData.value = null
    }

    fun deleteData(
        metaData: DataCollectionMeta,
        ctx: Context,
        successMsg: String? = "Eintrag gelöscht"
    ) {
        try {
            InternalStorage.deleteRecording(metaData, ctx)
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

    fun loadRecordingFromFile(metaData: DataCollectionMeta?, ctx: Context): List<SensorEventData>? {
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