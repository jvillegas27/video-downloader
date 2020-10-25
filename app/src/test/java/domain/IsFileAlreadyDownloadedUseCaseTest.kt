package domain

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import data.storage.LocalStorageManager
import org.junit.Assert
import org.junit.Test

class IsFileAlreadyDownloadedUseCaseTest {

    private val localStorageManager: LocalStorageManager = mock()
    private val isFileAlreadyDownloadedUseCase = IsFileAlreadyDownloadedUseCase(localStorageManager)

    @Test
    fun `Should return true if the file already exists`() {
        // Given
        val filePath = "http://domain/video.mp4"
        val fileName = "video.mp4"
        whenever(localStorageManager.getFileNameFromUrl(filePath)).thenReturn(fileName)
        whenever(localStorageManager.fileExists(fileName)).thenReturn(true)

        // When
        val result = isFileAlreadyDownloadedUseCase.run(filePath)

        // Then
        Assert.assertEquals(true, result)
    }

    @Test
    fun `Should return false if the file already does not`() {
        // Given
        val filePath = "http://domain/video.mp4"
        val fileName = "video.mp4"
        whenever(localStorageManager.getFileNameFromUrl(filePath)).thenReturn(fileName)
        whenever(localStorageManager.fileExists(fileName)).thenReturn(false)

        // When
        val result = isFileAlreadyDownloadedUseCase.run(filePath)

        // Then
        Assert.assertEquals(false, result)
    }
}