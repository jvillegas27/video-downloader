package data.network

import data.network.model.OnDownload
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import java.io.InputStream
import javax.inject.Inject

class DownloadRepositoryImpl @Inject constructor(
    private val downloadApiInterface: DownloadApiInterface
) :
    DownloadRepository {
    private val publishSubject = PublishSubject.create<OnDownload>()
    private var isPaused = false
    private var isCanceled = false
    private var disposable: Disposable? = null


    override fun downLoadFile(fileUrl: String): Observable<OnDownload> {
        disposable?.dispose()
        isCanceled = false
        isPaused = false

        disposable = downloadApiInterface.downloadFileFromUrl(fileUrl)
            .map { response ->
                response.body()?.let { body ->
                    var inputStream: InputStream? = null

                    try {
                        val data = ByteArray(4096)
                        inputStream = body.byteStream()
                        val fileSize = body.contentLength()

                        var count: Int
                        var fileSizeDownloaded = 0

                        while (inputStream.read(data).also { count = it } != -1) {
                            while (isPaused) {
                                //do nothing
                            }
                            if (isCanceled) {
                                return@map
                            }
                            fileSizeDownloaded += count
                            publishSubject.onNext(
                                OnDownload(
                                    data,
                                    count,
                                    fileSizeDownloaded,
                                    fileSize
                                )
                            )
                        }
                    } catch (ex: Exception) {
                        Timber.e(ex, "Exception getting data")
                        publishSubject.onError(ex)
                    } finally {
                        inputStream?.close()
                    }

                }
            }.subscribe({}, {
                publishSubject.onError(it)
            })


        return publishSubject.hide()
    }

    override fun cancelDownload() {
        isCanceled = true
        disposable?.dispose()
    }

    override fun pauseDownload() {
        isPaused = true
    }

    override fun resumeDownload() {
        isPaused = false
    }
}