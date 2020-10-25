package data.storage

import android.content.Context
import android.os.Environment.DIRECTORY_MOVIES
import java.io.File
import javax.inject.Inject

class LocalStorageManagerImpl @Inject constructor(
    private val context: Context,
    private val outputStreamFactory: OutputStreamFactory
) :
    LocalStorageManager {

    override fun getDownloadDirectory(): File? {
        return context.getExternalFilesDir(DIRECTORY_MOVIES)
    }

    override fun getDownloadFileName(fileName: String): File {
        return File(getDownloadDirectory()?.path, fileName)
    }

    override fun getFileNameFromUrl(fileUrl: String): String {
        return fileUrl.split("/").last()
    }

    override fun getStreamFromFile(fileName: String): OutputStreamFactory {
         outputStreamFactory.create(getDownloadFileName(fileName))
        return outputStreamFactory
    }

    override fun downloadChunkOfData(
        data: ByteArray,
        count: Int,
        outputStream: OutputStreamFactory
    ) {
        outputStream.write(data, count)
    }

    override fun fileExists(file: String): Boolean {
        return getDownloadFileName(file).exists()
    }

    override fun getDownloadedFiles(): List<File> {
        return getDownloadDirectory()?.walkTopDown()
            ?.toList()
            ?.filter { it.isDirectory.not() }
            ?: emptyList()
    }

    override fun deleteFile(filePath: String) {
        File(filePath).delete()
    }
}