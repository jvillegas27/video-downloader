package domain

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import data.storage.LocalStorageManager
import domain.model.VideoFileModel
import org.junit.Test
import java.io.File

class GetDownloadedFilesUseCaseTest {

    private val localStorageManager: LocalStorageManager = mock()
    private val getDownloadedFilesUseCase = GetDownloadedFilesUseCase(localStorageManager)

    @Test
    fun `Should return the correct list for downloaded files`() {
        // Given
        val listOfFiles = listOf(
            File("/system/fileA.mp4"),
            File("/system/fileB.mp4")
        )
        val expectResult = listOf(
            VideoFileModel("fileA.mp4", "/system/fileA.mp4"),
            VideoFileModel("fileB.mp4", "/system/fileB.mp4")
        )
        whenever(localStorageManager.getDownloadedFiles()).thenReturn(listOfFiles)

        // Then
        val result = getDownloadedFilesUseCase.run().test()

        // When
        result.assertValue { expectResult.containsAll(expectResult) }
            .assertComplete()
            .assertNoErrors()

    }
}