package domain

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import data.network.DownloadRepository
import data.network.model.OnDownload
import data.storage.LocalStorageManager
import data.storage.OutputStreamFactory
import domain.model.DownloadViewModel
import io.reactivex.Observable
import org.junit.Test
import java.io.File

class DownloadFileUseCaseTest {

    private val downloadRepository: DownloadRepository = mock()
    private val localStorageManager: LocalStorageManager = mock()
    private val outputStreamFactory: OutputStreamFactory = mock()
    private val downloadFileUseCase = DownloadFileUseCaseImpl(
        downloadRepository,
        localStorageManager
    )

    @Test
    fun `Should return Complete download event`() {
        // Given
        val fileUrl = "downloadUrl"
        val fileName = "video.mp4"
        val onDownload = OnDownload(ByteArray(4096), 2, 100, 100)
        whenever(localStorageManager.getFileNameFromUrl(fileUrl)).thenReturn(fileName)
        whenever(localStorageManager.getStreamFromFile(fileName)).thenReturn(outputStreamFactory)
        whenever(downloadRepository.downLoadFile(fileUrl)).thenReturn(
            Observable.just(onDownload)
        )
        whenever(localStorageManager.getDownloadFileName(fileName)).thenReturn(File("/sdcard/video.mp3"))

        // When
        val result = downloadFileUseCase.download(fileUrl).test()

        // Then
        result.assertValue {
            it is DownloadViewModel.OnDownloadComplete &&
                    it.filePath == "\\sdcard\\video.mp3"
        }
            .assertNoErrors()
            .assertComplete()
    }

    @Test
    fun `Should return the correct progress update`() {
        // Given
        val fileUrl = "downloadUrl"
        val fileName = "video.mp4"
        val onDownload = OnDownload(ByteArray(4096), 2, 10, 100)
        whenever(localStorageManager.getFileNameFromUrl(fileUrl)).thenReturn(fileName)
        whenever(localStorageManager.getStreamFromFile(fileName)).thenReturn(outputStreamFactory)
        whenever(downloadRepository.downLoadFile(fileUrl)).thenReturn(
            Observable.just(onDownload)
        )
        whenever(localStorageManager.getDownloadFileName(fileName)).thenReturn(File("/sdcard/video.mp3"))

        // When
        val result = downloadFileUseCase.download(fileUrl).test()

        // Then
        result.assertValue {
            it is DownloadViewModel.OnProgress &&
                    it.progress == 10
        }
            .assertNoErrors()
            .assertComplete()
    }

    @Test
    fun `Should call saveStreamToFile`() {
        // Given
        val fileUrl = "downloadUrl"
        val fileName = "video.mp4"
        val onDownload = OnDownload(ByteArray(4096), 2, 100, 100)
        whenever(localStorageManager.getFileNameFromUrl(fileUrl)).thenReturn(fileName)
        whenever(localStorageManager.getStreamFromFile(fileName)).thenReturn(outputStreamFactory)
        whenever(downloadRepository.downLoadFile(fileUrl)).thenReturn(
            Observable.just(onDownload)
        )
        whenever(localStorageManager.getDownloadFileName(fileName)).thenReturn(File("/sdcard/video.mp3"))

        // When
        downloadFileUseCase.download(fileUrl).test()

        // Then
        verify(localStorageManager).downloadChunkOfData(ByteArray(4096), 2, outputStreamFactory)
    }

    @Test
    fun `should call resume`() {
        // Given

        // When
        downloadFileUseCase.resume()

        // Then
        verify(downloadRepository).resumeDownload()

    }

    @Test
    fun `should call pause`() {
        // Given

        // When
        downloadFileUseCase.pause()

        // Then
        verify(downloadRepository).pauseDownload()
    }

    @Test
    fun `should call cancel`() {
        // Given

        // When
        downloadFileUseCase.cancel()

        // Then
        verify(downloadRepository).cancelDownload()
    }
}