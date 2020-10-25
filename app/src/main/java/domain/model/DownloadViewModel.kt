package domain.model

sealed class DownloadViewModel {
    data class OnProgress(val progress: Int) : DownloadViewModel()
    data class OnDownloadComplete(val filePath: String) : DownloadViewModel()
}