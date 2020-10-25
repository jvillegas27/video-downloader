package domain

import data.storage.LocalStorageManager
import io.reactivex.Completable
import javax.inject.Inject

class DeleteFileUseCase @Inject constructor(private val localStorageManager: LocalStorageManager) {

    fun run(filePath: String): Completable {
        return Completable.fromCallable {
            localStorageManager.deleteFile(filePath)
        }
    }
}