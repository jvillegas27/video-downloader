package data.storage

import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import javax.inject.Inject

class OutputStreamFactoryImpl @Inject constructor() : OutputStreamFactory {
    private var outputStream: OutputStream? = null

    override fun create(fileName: File): OutputStream {
        return FileOutputStream(fileName).also {
            outputStream = it
        }
    }

    override fun write(data: ByteArray, count: Int) {
        outputStream?.write(data, 0, count)
    }
}