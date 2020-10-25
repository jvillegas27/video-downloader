package domain

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import data.storage.LocalStorageManager
import org.junit.Test

class DeleteFileUseCaseTest {

    private val localStorageManager: LocalStorageManager = mock()
    private val deleteFileUseCase = DeleteFileUseCase(localStorageManager)

    @Test
    fun `Should call delete file`() {
        // Given
        val filePath = "anyFile"
        // When
        deleteFileUseCase.run(filePath).test()

        // Then
        verify(localStorageManager).deleteFile("anyFile")
    }
}