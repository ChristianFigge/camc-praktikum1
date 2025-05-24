package com.example.camc_praktikum1.data

import android.content.Context
import android.icu.text.SimpleDateFormat
import com.example.camc_praktikum1.data.models.ExportSessionData
import com.example.camc_praktikum1.data.models.RecordingMetaData
import com.example.camc_praktikum1.data.models.SensorEventData
import com.example.camc_praktikum1.data.util.compress
import com.example.camc_praktikum1.data.util.decompress
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.FileNotFoundException
import java.io.IOException
import java.io.File
import java.util.Date
import java.util.Locale

class InternalStorage {
    companion object {

        private val INDEX_FILENAME = "data_index.json"
        private var sdf = SimpleDateFormat("dd-MM_HH-mm", Locale.getDefault())

        @Throws(FileNotFoundException::class)
        fun readFile(fileName: String, ctx: Context): ByteArray {
            return ctx.openFileInput(fileName).use { it.readBytes() }
        }

        @Throws(FileNotFoundException::class)
        fun readTextFile(fileName: String, ctx: Context): String {
            return ctx.openFileInput(fileName).bufferedReader().use { it.readText() }
        }

        @Throws(FileNotFoundException::class, IOException::class)
        fun writeDataToFile(data: ByteArray, fileName: String, ctx: Context) {
            ctx.openFileOutput(fileName, Context.MODE_PRIVATE).use { it.write(data) }
        }

        @Throws(FileNotFoundException::class, IOException::class)
        fun writeTextFile(text: String, fileName: String, ctx: Context) {
            writeDataToFile(text.toByteArray(), fileName, ctx)
        }

        @Throws(SerializationException::class, FileNotFoundException::class, IOException::class)
        inline fun <reified T> saveDataAsJsonFile(data: T, fileName: String, ctx: Context, compress: Boolean = false) {
            val jsonString = Json.encodeToString(data)
            if(compress)
                writeDataToFile(jsonString.compress(), fileName, ctx)
            else
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
            val jsonString = readFile(fileName, ctx).decompress()
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

        // TODO add throws
        fun getSessionExportData(metaData: RecordingMetaData, ctx: Context): Pair<ByteArray, String> {
            // get all recordings with the same sessionId from index
            val sessionSensorNames: MutableList<String> = mutableListOf()
            val sessionRecordings: MutableList<List<SensorEventData>> = mutableListOf()
            readRecordingIndex(ctx).forEach { recMetaData ->
                if(recMetaData.sessionId == metaData.sessionId) {
                    sessionSensorNames.add(recMetaData.sensorName)
                    sessionRecordings.add(loadRecordingFromFile(recMetaData.fileName, ctx))
                }
            }
            // create data object, encode it to json and zlib-compress
            val exportBytes = Json.encodeToString(
                ExportSessionData(
                    sessionId = metaData.sessionId,
                    createdAt = metaData.createdAt,
                    durationMs = metaData.durationMs,
                    sensorTypes = sessionSensorNames,
                    sensorRecordings = sessionRecordings,
                )
            ).compress()

            val exportFileName = "sensorDaten_${sdf.format(Date(metaData.createdAt))}.json.zlib"
            return Pair(exportBytes, exportFileName)
        }
    }
}