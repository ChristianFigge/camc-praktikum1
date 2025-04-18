package com.example.camc_praktikum1.data

import android.content.Context
import com.example.camc_praktikum1.data.models.DataCollectionMeta
import com.example.camc_praktikum1.data.models.SensorEventData
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.FileNotFoundException
import java.io.IOException
import java.io.File

class StorageIO {
    companion object{

        private val INDEX_FILENAME = "data_index.json"

        @Throws(FileNotFoundException::class, IOException::class)
        fun writeDataToFile(data: ByteArray, fileName: String, ctx: Context) {
            ctx.openFileOutput(fileName, Context.MODE_PRIVATE).use {
                it.write(data)
            }
        }

        @Throws(FileNotFoundException::class)
        fun readTextFile(fileName: String, ctx: Context): String {
            return ctx.openFileInput(fileName).bufferedReader().readText()
        }

        @Throws(FileNotFoundException::class, IOException::class)
        fun writeTextFile(text: String, fileName:String, ctx: Context) {
            writeDataToFile(text.toByteArray(), fileName, ctx)
        }

        @Throws(
            FileNotFoundException::class,
            IOException::class,
            SerializationException::class,
            IllegalArgumentException::class
        )
        fun readSensorRecording(fileName: String, ctx: Context): List<SensorEventData> {
            val jsonString = readTextFile(fileName, ctx)
            return Json.decodeFromString(jsonString)
        }

        fun deleteRecording(metaData: DataCollectionMeta, ctx: Context) {
            if(ctx.fileList().contains(metaData.fileName)) {
                val file = File(ctx.filesDir, metaData.fileName)
                file.delete()

                // Update Data Index
                val dataIndex = readCollectionIndex(ctx)
                val idx_to_delete = mutableListOf<Int>()

                // kann zwar theoretisch nur 1 Eintrag existieren aber was soll der geiz
                dataIndex.forEachIndexed { idx, item ->
                    if(item.fileName == metaData.fileName) {
                        idx_to_delete.add(idx)
                    }
                }
                idx_to_delete.forEach { dataIndex.removeAt(it) }
                updateCollectionIndex(dataIndex, ctx)
            }
        }

        fun readCollectionIndex(ctx: Context): MutableList<DataCollectionMeta> {
            var colIdx = mutableListOf<DataCollectionMeta>()
            try {
                val fileContent: String = readTextFile(INDEX_FILENAME, ctx)
                colIdx = Json.decodeFromString(fileContent)
            } catch(fileEx: FileNotFoundException) {
                //pass
            }
            return colIdx
        }

        private fun updateCollectionIndex(newCollectionIndex: List<DataCollectionMeta>, ctx: Context) {
            val jsonString = Json.encodeToString(newCollectionIndex)
            writeTextFile(jsonString, INDEX_FILENAME, ctx)
        }

        @Throws(FileNotFoundException::class)
        fun readJsonFromStorage(fileName: String, ctx: Context) : String {
            return ctx.openFileInput(fileName).bufferedReader().readText()
        }


    }
}