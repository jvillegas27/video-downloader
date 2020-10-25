package app

import android.Manifest
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import app.model.NavigationEvent
import app.permission.PermissionsManager
import app.viewmodel.MainViewModel
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.vanniktech.rxpermission.Permission
import domain.*
import domain.model.DownloadViewModel
import io.reactivex.Observable
import io.reactivex.Single
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class MainViewModelTest {

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    private val permissionsManager: PermissionsManager = mock()
    private val downloadFileUseCase: DownloadFileUseCase = mock()
    private val isFileAlreadyDownloadedUseCase: IsFileAlreadyDownloadedUseCase = mock()
    private val getDownloadedFilesUseCase: GetDownloadedFilesUseCase = mock()
    private val deleteFileUseCase: DeleteFileUseCase = mock()
    private val getIsValidUrlUseCase: GetIsValidUrlUseCase = mock()

    private val viewModel = MainViewModel(
        permissionsManager,
        downloadFileUseCase,
        isFileAlreadyDownloadedUseCase,
        getDownloadedFilesUseCase,
        deleteFileUseCase,
        getIsValidUrlUseCase
    )

    @Test
    fun `Should send empty url event`() {
        // Given
        val observer: Observer<NavigationEvent> = mock()
        viewModel.navigationEvent.observeForever(observer)

        // When
        viewModel.downloadVideo("")

        // Then
        verify(observer).onChanged(NavigationEvent.OnFileUrlEmpty)
    }

    @Test
    fun `Should send file already downloaded event`() {
        // Given
        val observer: Observer<NavigationEvent> = mock()
        viewModel.navigationEvent.observeForever(observer)
        whenever(isFileAlreadyDownloadedUseCase.run("fileUrl")).thenReturn(true)

        // When
        viewModel.downloadVideo("fileUrl")

        // Then
        verify(observer).onChanged(NavigationEvent.OnFileAlreadyDownloaded)
    }

    @Test
    fun `Should send storage permission denied event`() {
        // Given
        val observer: Observer<NavigationEvent> = mock()
        viewModel.navigationEvent.observeForever(observer)
        val fileUrl = "fileUrl"
        whenever(isFileAlreadyDownloadedUseCase.run(fileUrl)).thenReturn(false)
        whenever(permissionsManager.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)).thenReturn(
            Single.just(Permission.denied(Manifest.permission.WRITE_EXTERNAL_STORAGE))
        )
        whenever(getIsValidUrlUseCase.run(fileUrl)).thenReturn(true)

        // When
        viewModel.downloadVideo(fileUrl)

        // Then
        verify(observer).onChanged(NavigationEvent.OnPermissionDenied)
    }

    @Test
    fun `Should invalid url event`() {
        // Given
        val observer: Observer<NavigationEvent> = mock()
        viewModel.navigationEvent.observeForever(observer)
        val fileUrl = "fileUrl"
        whenever(isFileAlreadyDownloadedUseCase.run(fileUrl)).thenReturn(false)
        whenever(getIsValidUrlUseCase.run(fileUrl)).thenReturn(false)

        // When
        viewModel.downloadVideo(fileUrl)

        // Then
        verify(observer).onChanged(NavigationEvent.OnUrlNotValid)
    }

    @Test
    fun `Should send the correct progress download event`() {
        // Given
        val observer: Observer<Int> = mock()
        viewModel.progressUpdate.observeForever(observer)
        val fileUrl = "fileUrl"
        whenever(isFileAlreadyDownloadedUseCase.run(fileUrl)).thenReturn(false)
        whenever(permissionsManager.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)).thenReturn(
            Single.just(Permission.granted(Manifest.permission.WRITE_EXTERNAL_STORAGE))
        )
        whenever(getIsValidUrlUseCase.run(fileUrl)).thenReturn(true)
        whenever(downloadFileUseCase.download(fileUrl)).thenReturn(
            Observable.just(
                DownloadViewModel.OnProgress(23)
            )
        )

        // When
        viewModel.downloadVideo(fileUrl)

        // Then
        verify(observer).onChanged(23)
    }

    @Test
    fun `Should send download complete event`() {
        // Given
        val observer: Observer<NavigationEvent> = mock()
        viewModel.navigationEvent.observeForever(observer)
        val fileUrl = "fileUrl"
        whenever(isFileAlreadyDownloadedUseCase.run(fileUrl)).thenReturn(false)
        whenever(permissionsManager.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)).thenReturn(
            Single.just(Permission.granted(Manifest.permission.WRITE_EXTERNAL_STORAGE))
        )
        whenever(getIsValidUrlUseCase.run(fileUrl)).thenReturn(true)
        whenever(downloadFileUseCase.download(fileUrl)).thenReturn(
            Observable.just(
                DownloadViewModel.OnDownloadComplete("/sdcard/path")
            )
        )

        // When
        viewModel.downloadVideo(fileUrl)

        // Then
        verify(observer).onChanged(NavigationEvent.OnDownloadComplete("/sdcard/path"))
    }

    @Test
    fun `Should send IOException event`() {
        // Given
        val observer: Observer<NavigationEvent> = mock()
        viewModel.navigationEvent.observeForever(observer)
        val fileUrl = "fileUrl"
        whenever(isFileAlreadyDownloadedUseCase.run(fileUrl)).thenReturn(false)
        whenever(permissionsManager.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)).thenReturn(
            Single.just(Permission.granted(Manifest.permission.WRITE_EXTERNAL_STORAGE))
        )
        whenever(getIsValidUrlUseCase.run(fileUrl)).thenReturn(true)
        whenever(downloadFileUseCase.download(fileUrl)).thenReturn(
            Observable.error(IOException())
        )

        // When
        viewModel.downloadVideo(fileUrl)

        // Then
        verify(observer).onChanged(NavigationEvent.OnDownloadConnectionError)
    }

    @Test
    fun `Should send Genric Error event`() {
        // Given
        val observer: Observer<NavigationEvent> = mock()
        viewModel.navigationEvent.observeForever(observer)
        val fileUrl = "fileUrl"
        whenever(isFileAlreadyDownloadedUseCase.run(fileUrl)).thenReturn(false)
        whenever(permissionsManager.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)).thenReturn(
            Single.just(Permission.granted(Manifest.permission.WRITE_EXTERNAL_STORAGE))
        )
        whenever(getIsValidUrlUseCase.run(fileUrl)).thenReturn(true)
        whenever(downloadFileUseCase.download(fileUrl)).thenReturn(
            Observable.error(IllegalAccessError())
        )

        // When
        viewModel.downloadVideo(fileUrl)

        // Then
        verify(observer).onChanged(NavigationEvent.OnDownloadGenericError)
    }

    @Test
    fun `Should call delete file `() {
        // Given
        val fileUrl = "fileUrl"
        whenever(permissionsManager.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)).thenReturn(
            Single.just(Permission.granted(Manifest.permission.WRITE_EXTERNAL_STORAGE))
        )

        // When
        viewModel.deleteFile(fileUrl)

        // Then
        verify(deleteFileUseCase).run(fileUrl)
        verify(getDownloadedFilesUseCase).run()
    }

    @Test
    fun `Should call pause `() {
        // Given

        // When
        viewModel.onPauseClicked()

        // Then
        verify(downloadFileUseCase).pause()
    }

    @Test
    fun `Should call onResumeClicked `() {
        // Given

        // When
        viewModel.onResumeClicked()

        // Then
        verify(downloadFileUseCase).resume()
    }

    @Test
    fun `Should call cancel and get downloaded files`() {
        // Given
        whenever(permissionsManager.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)).thenReturn(
            Single.just(Permission.granted(Manifest.permission.WRITE_EXTERNAL_STORAGE))
        )

        // When
        viewModel.onCancel()

        // Then
        verify(downloadFileUseCase).cancel()
        verify(getDownloadedFilesUseCase).run()
    }
}