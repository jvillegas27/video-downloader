package domain

import data.network.DownloadRepository
import data.network.model.OnDownload
import data.storage.LocalStorageManager
import data.storage.OutputStreamFactory
import domain.model.DownloadViewModel
import io.reactivex.Observable
import javax.inject.Inject

class DownloadFileUseCaseImpl @Inject constructor(
    private val repository: DownloadRepository,
    private val localStorageManager: LocalStorageManager
) : DownloadFileUseCase {

    override fun download(fileUrl: String): Observable<DownloadViewModel> {
        val fileName = localStorageManager.getFileNameFromUrl(fileUrl)
        val outputStream = localStorageManager.getStreamFromFile(fileName)

        return repository.downLoadFile(fileUrl)
            .flatMap {
                saveStreamToFile(it, outputStream)
                val progress =
                    ((it.fileSizeDownloaded / it.fileSize.toDouble()) * 100).toInt()
                val event = if (progress >= 100) {
                    DownloadViewModel.OnDownloadComplete(
                        localStorageManager.getDownloadFileName(
                            fileName
                        ).path
                    )
                } else {
                    DownloadViewModel.OnProgress(progress)
                }
                Observable.just(event)
            }
    }

    override fun resume() {
        repository.resumeDownload()
    }

    override fun pause() {
        repository.pauseDownload()
    }

    override fun cancel() {
        repository.cancelDownload()
    }

    private fun saveStreamToFile(
        downloadEvent: OnDownload,
        outputStream: OutputStreamFactory
    ) {
        localStorageManager.downloadChunkOfData(
            downloadEvent.data,
            downloadEvent.count,
            outputStream
        )
    }
}