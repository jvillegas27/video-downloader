package domain

import data.storage.LocalStorageManager
import domain.model.VideoFileModel
import io.reactivex.Single
import javax.inject.Inject

class GetDownloadedFilesUseCase @Inject constructor(private val localStorageManager: LocalStorageManager) {

    fun run(): Single<List<VideoFileModel>> {
        return Single.fromCallable {
            localStorageManager.getDownloadedFiles()
        }
            .map { list ->
            list.map { VideoFileModel(it.name, it.path) }
        }
    }
}