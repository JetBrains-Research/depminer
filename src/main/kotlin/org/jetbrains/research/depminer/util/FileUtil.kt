package org.jetbrains.research.depminer.util

import java.io.BufferedInputStream
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream


@Throws(IOException::class)
fun countLines(filename: String): Int {
    /***
     * A function to efficiently compute the number of lines ('\n' symbols) in a multi-line source code file
     * Translated from the original Java method from https://stackoverflow.com/questions/453018/number-of-lines-in-a-file-in-java
     * Answer by @DMulligan
     */
    val `is`: InputStream = BufferedInputStream(FileInputStream(filename))
    return `is`.use { `is` ->
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
    }
}