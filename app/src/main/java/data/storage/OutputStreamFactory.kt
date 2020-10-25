package data.storage

import java.io.File
import java.io.OutputStream

interface OutputStreamFactory {

    fun create(fileName: File): OutputStream
    fun write(data: ByteArray, count: Int)
}