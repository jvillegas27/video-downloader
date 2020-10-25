package domain

import data.storage.LocalStorageManager
import javax.inject.Inject

class IsFileAlreadyDownloadedUseCase @Inject constructor(private val localStorageManager: LocalStorageManager) {

    fun run(fileUrl: String): Boolean {
        val file = localStorageManager.getFileNameFromUrl(fileUrl)
        return localStorageManager.fileExists(file)
    }
}