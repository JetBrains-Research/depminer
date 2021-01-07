package org.jetbrains.research.depminer.util

import java.io.*
import java.nio.charset.Charset


@Throws(IOException::class)
fun countLines(filename: String?): Int {
    val `is`: InputStream = BufferedInputStream(FileInputStream(filename))
    return try {
        val c = ByteArray(1024)
        var count = 0
        var readChars = 0
        var endsWithoutNewLine = false
        while (`is`.read(c).also { readChars = it } != -1) {
            for (i in 0 until readChars) {
                if (c[i] == '\n'.toByte()) ++count
            }
            endsWithoutNewLine = c[readChars - 1] != '\n'.toByte()
        }
        if (endsWithoutNewLine) {
            ++count
        }
        count
    } finally {
        `is`.close()
    }
}

@Throws(IOException::class)
fun getFileLength(it: File): Int {
    val reader = BufferedReader(InputStreamReader(FileInputStream(it), Charset.defaultCharset()))
    var charCount = 0
    while (reader.read() > -1) {
        charCount++
    }
    reader.close()
    return charCount
}
