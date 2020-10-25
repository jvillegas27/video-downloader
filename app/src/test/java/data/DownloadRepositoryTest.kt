package data

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import data.network.DownloadApiInterface
import data.network.DownloadRepositoryImpl
import io.reactivex.Observable
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Test
import java.io.IOException

class DownloadRepositoryTest {

    private val downloadApiInterface: DownloadApiInterface = mock()
    private val downloadRepository = DownloadRepositoryImpl(downloadApiInterface)

    @Test
    fun `Download should not throw errors`() {
        // Given
        val fileUrl = "file"
        val responseBody = "response".toResponseBody("video/*".toMediaType())
        val response = retrofit2.Response.success(responseBody)
        whenever(downloadApiInterface.downloadFileFromUrl(fileUrl)).thenReturn(
            Observable.just(
                response
            )
        )

        // When
        val downloadFile = downloadRepository.downLoadFile(fileUrl).test()

        // Then
        downloadFile.assertNoErrors()
    }

    @Test
    fun `Download should throw errors`() {
        // Given
        val fileUrl = "file"
        whenever(downloadApiInterface.downloadFileFromUrl(fileUrl)).thenReturn(
            Observable.error(IOException())
        )

        // When
        val downloadFile = downloadRepository.downLoadFile(fileUrl).test()

        // Then
        downloadFile.assertError(IOException::class.java)
    }
}