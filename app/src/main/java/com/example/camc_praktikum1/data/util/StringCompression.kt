package com.example.camc_praktikum1.data.util

import java.io.ByteArrayOutputStream
import java.util.zip.Deflater
import java.util.zip.Inflater

/*
 * Full credit https://gist.github.com/marcouberti/40dbbd836562b35ace7fb2c627b0f34f
 * TO-DO testen: InflaterInputStream(inputBytes.inputStream()).bufferedReader().use { it.readText() }
 */

/**
 * Compress a string using ZLIB.
 *
 * @return an UTF-8 encoded byte array.
 */
fun String.compress(): ByteArray {
    val input = this.toByteArray(charset("UTF-8"))

    // Compress the bytes
    // 1 to 4 bytes/char for UTF-8
    val output = ByteArray(input.size * 4)
    val compressor = Deflater().apply {
        setInput(input)
        finish()
    }
    val compressedDataLength: Int = compressor.deflate(output)
    return output.copyOfRange(0, compressedDataLength)
}

/**
 * Decompress a byte array using ZLIB.
 *
 * @return an UTF-8 encoded string.
 */
fun ByteArray.decompress(): String {
    val inflater = Inflater()
    val outputStream = ByteArrayOutputStream()

    return outputStream.use {
        val buffer = ByteArray(1024)

        inflater.setInput(this)

        var count = -1
        while (count != 0) {
            count = inflater.inflate(buffer)
            outputStream.write(buffer, 0, count)
        }

        inflater.end()
        outputStream.toString("UTF-8")
    }
}