package data.storage

import java.io.File

interface LocalStorageManager {
    fun getDownloadFileName(fileName: String): File
    fun getDownloadDirectory(): File?
    fun getFileNameFromUrl(fileUrl: String): String
    fun getStreamFromFile(fileName: String): OutputStreamFactory
    fun fileExists(file: String): Boolean
    fun getDownloadedFiles(): List<File>
    fun deleteFile(filePath: String)
    fun downloadChunkOfData(data: ByteArray, count: Int, outputStream: OutputStreamFactory)
}