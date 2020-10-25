# Video Downloader 
This app is an example of how to download media from the web, tech stack

 - Retrofit
 - OkHttp
 - MVVM with clean architecture
 - Mockito
 - Dagger2
 - Compiled in Android Studio Stable 4.1
 - RxKotlin

## Features
* Download a video and after the download is complete it will open a full screen with the video
* It will display a list of the downloaded videos
* Click in any video downloaded will open a new window to play the video
* Swipe to delete video
* Pause/resume video
* cancel download

## Limitations
* The app will download the requested URL from the network, however it will do it when the app (Activity) is in foreground, the suggested way to do it, it's with a Foreground service, this will take longer so for now this is only for the activity.
* The app does not verify mimetype for videos, it will download any file, this could be improved

## Improvements
* The file for the video is created always, not matter if the download is completed or not, the best would be download in a temporal folder and then move it to the final folder
* Add a service instead the activity for download, the logic it's wrapped in uses cased and the view model, so should not take that long.
* Support landscape
* When there is an error in the network the loading will still be displayed, the user must "cancel"
* I'm using 4 buttons for every action (download, cancel, pause,resume) to keep simple the logic and every button do something, this could be improved in the future
* Pause/Resume could be improved, I'm doing a loop until the user cancel/resume, probably there is a better solution... 

  