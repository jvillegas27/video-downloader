package data.network

import data.network.model.OnDownload
import io.reactivex.Observable

interface DownloadRepository {

    fun downLoadFile(fileUrl: String): Observable<OnDownload>
    fun pauseDownload()
    fun resumeDownload()
    fun cancelDownload()
}