package com.example.camc_praktikum1.data

import android.content.Context
import com.example.camc_praktikum1.data.models.RecordingMetaData
import com.example.camc_praktikum1.data.models.SensorEventData
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.FileNotFoundException
import java.io.IOException
import java.io.File

class InternalStorage {
    companion object {

        private val INDEX_FILENAME = "data_index.json"

        @Throws(FileNotFoundException::class)
        fun readTextFile(fileName: String, ctx: Context): String {
            return ctx.openFileInput(fileName).bufferedReader().readText()
        }

        @Throws(FileNotFoundException::class, IOException::class)
        fun writeDataToFile(data: ByteArray, fileName: String, ctx: Context) {
            ctx.openFileOutput(fileName, Context.MODE_PRIVATE).use {
                it.write(data)
            }
        }

        @Throws(FileNotFoundException::class, IOException::class)
        fun writeTextFile(text: String, fileName: String, ctx: Context) {
            writeDataToFile(text.toByteArray(), fileName, ctx)
        }

        @Throws(SerializationException::class, FileNotFoundException::class, IOException::class)
        inline fun <reified T> saveDataAsJsonFile(data: T, fileName: String, ctx: Context) {
            val jsonString = Json.encodeToString(data)
            writeTextFile(jsonString, fileName, ctx)
        }

        @Throws(FileNotFoundException::class, NullPointerException::class, SecurityException::class)
        fun deleteFile(fileName: String, ctx: Context): Boolean {
            if (ctx.fileList().contains(fileName)) {
                val file = File(ctx.filesDir, fileName)
                return file.delete()
            } else throw FileNotFoundException("File $fileName does not exist in local storage.")
        }

        @Throws(FileNotFoundException::class, SerializationException::class, IllegalArgumentException::class)
        fun loadRecordingFromFile(fileName: String, ctx: Context): List<SensorEventData> {
            val jsonString = readTextFile(fileName, ctx)
            return Json.decodeFromString(jsonString)
        }

        @Throws(
            FileNotFoundException::class,
            NullPointerException::class,
            SecurityException::class,
            SerializationException::class,
            IllegalArgumentException::class,
            IOException::class
        )
        fun deleteRecordingFromStorage(metaData: RecordingMetaData, ctx: Context) {
            deleteFile(metaData.fileName, ctx)

            // Update Data Index
            val dataIndex = readRecordingIndex(ctx)
            val idx_to_delete = mutableListOf<Int>()

            // kann zwar theoretisch nur 1 Eintrag existieren aber was soll der geiz
            dataIndex.forEachIndexed { idx, item ->
                if (item.fileName == metaData.fileName) {
                    idx_to_delete.add(idx)
                }
            }
            idx_to_delete.forEach { dataIndex.removeAt(it) }
            updateRecordingIndex(dataIndex, ctx)
        }

        /**
         * Tries to obtain record index from the app's storage (currently a JSON file).
         * Returns an empty index if the file doesn't exist yet.
         */
        @Throws(SerializationException::class, IllegalArgumentException::class)
        fun readRecordingIndex(ctx: Context): MutableList<RecordingMetaData> {
            try {
                val fileContent: String = readTextFile(INDEX_FILENAME, ctx)
                return Json.decodeFromString(fileContent)
            } catch (fileEx: FileNotFoundException) {
                return mutableListOf<RecordingMetaData>()
            }
        }

        @Throws(SerializationException::class, FileNotFoundException::class, IOException::class)
        fun updateRecordingIndex(newCollectionIndex: List<RecordingMetaData>, ctx: Context) {
            saveDataAsJsonFile(newCollectionIndex, INDEX_FILENAME, ctx)
        }
    }
}