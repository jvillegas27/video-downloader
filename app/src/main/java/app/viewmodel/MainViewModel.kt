package app.viewmodel

import android.Manifest
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import app.model.NavigationEvent
import app.permission.PermissionsManager
import app.utils.SingleLiveEvent
import com.vanniktech.rxpermission.Permission
import domain.*
import domain.model.DownloadViewModel
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val permissionsManager: PermissionsManager,
    private val downloadFileUseCase: DownloadFileUseCase,
    private val isFileAlreadyDownloadedUseCase: IsFileAlreadyDownloadedUseCase,
    private val getDownloadedFilesUseCase: GetDownloadedFilesUseCase,
    private val deleteFileUseCase: DeleteFileUseCase,
    private val getIsValidUrlUseCase: GetIsValidUrlUseCase
) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val _navigationEvent = SingleLiveEvent<NavigationEvent>()
    val navigationEvent: LiveData<NavigationEvent>
        get() = _navigationEvent

    private val _progressUpdate = SingleLiveEvent<Int>()
    val progressUpdate: LiveData<Int>
        get() = _progressUpdate

    fun downloadVideo(fileUrl: String) {
        when {
            fileUrl.isEmpty() -> {
                _navigationEvent.postValue(NavigationEvent.OnFileUrlEmpty)
            }
            isFileAlreadyDownloaded(fileUrl) -> {
                _navigationEvent.postValue(NavigationEvent.OnFileAlreadyDownloaded)
            }
            isValidUrl(fileUrl) -> {
                askForStoragePermissionAndDownload(fileUrl)
            }
            else -> {
                _navigationEvent.postValue(NavigationEvent.OnUrlNotValid)
            }
        }
    }


    private fun isFileAlreadyDownloaded(fileUrl: String): Boolean {
        return isFileAlreadyDownloadedUseCase.run(fileUrl)
    }

    private fun askForStoragePermissionAndDownload(fileUrl: String) {
        permissionsManager.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .subscribe({
                if (it.state() == Permission.State.GRANTED) {
                    downloadFile(fileUrl)
                } else {
                    _navigationEvent.postValue(NavigationEvent.OnPermissionDenied)
                    Timber.e("Permission denied")
                }

            }, {
                Timber.e(it)
            })
            .also { compositeDisposable.add(it) }
    }

    private fun isValidUrl(fileUrl: String): Boolean {
        return getIsValidUrlUseCase.run(fileUrl)
    }

    private fun downloadFile(fileUrl: String) {
        downloadFileUseCase.download(fileUrl)
            .doOnSubscribe {
                _navigationEvent.postValue(NavigationEvent.OnLoading)
            }
            .subscribe({
                when (it) {
                    is DownloadViewModel.OnProgress -> processUpdate(it.progress)
                    is DownloadViewModel.OnDownloadComplete -> processDownloadComplete(it.filePath)
                }
            }, {
                val event = if (it is IOException) {
                    NavigationEvent.OnDownloadConnectionError
                } else {
                    NavigationEvent.OnDownloadGenericError
                }
                _navigationEvent.postValue(event)
                Timber.e(it, "network error")
            })
            .also { compositeDisposable.add(it) }

    }

    private fun processDownloadComplete(filePath: String) {
        _navigationEvent.postValue(NavigationEvent.OnDownloadComplete(filePath))
    }

    private fun processUpdate(progress: Int) {
        Timber.v("Progress downloaded $progress")
        _progressUpdate.postValue(progress)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
        downloadFileUseCase.pause()
    }

    fun getDownloadedVideos() {
        permissionsManager.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .filter { it.state() == Permission.State.GRANTED }
            .toSingle()
            .flatMap { getDownloadedFilesUseCase.run() }
            .subscribe({
                _navigationEvent.postValue(NavigationEvent.OnDownloadedVideos(it))
            }, {
                Timber.e(it, "Error getting videos")
            })
            .also { compositeDisposable.add(it) }
    }

    fun deleteFile(filePath: String) {
        permissionsManager.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .filter { it.state() == Permission.State.GRANTED }
            .flatMapCompletable { deleteFileUseCase.run(filePath) }
            .doAfterTerminate { getDownloadedVideos() }
            .subscribe({
            }, {}).also { compositeDisposable.add(it) }
    }

    fun onPauseClicked() {
        downloadFileUseCase.pause()
    }

    fun onResumeClicked() {
        downloadFileUseCase.resume()
    }

    fun onCancel() {
        downloadFileUseCase.cancel()
        getDownloadedVideos()
    }
}