package app.model

import domain.model.VideoFileModel

sealed class NavigationEvent {
    object OnPermissionDenied : NavigationEvent()
    object OnLoading : NavigationEvent()
    object OnFileUrlEmpty : NavigationEvent()
    object OnFileAlreadyDownloaded : NavigationEvent()
    object OnUrlNotValid : NavigationEvent()
    object OnDownloadConnectionError : NavigationEvent()
    object OnDownloadGenericError : NavigationEvent()

    data class OnDownloadComplete(val filePath: String) : NavigationEvent()
    class OnDownloadedVideos(val videoPaths: List<VideoFileModel>) : NavigationEvent()
}