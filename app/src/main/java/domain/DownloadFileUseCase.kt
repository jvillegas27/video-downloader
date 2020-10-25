package domain

import domain.model.DownloadViewModel
import io.reactivex.Observable

interface DownloadFileUseCase {
    fun download(fileUrl: String): Observable<DownloadViewModel>
    fun pause()
    fun resume()
    fun cancel()
}