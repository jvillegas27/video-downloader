package app

import android.net.Uri
import android.os.Bundle
import android.widget.MediaController
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_video_player.*

class ActivityVideo : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)
        intent?.getStringExtra(ARGS_KEY_FILE_PATH)?.let {
            createMediaController(it)
        } ?: kotlin.run {
            Toast.makeText(this, R.string.error_general, Toast.LENGTH_SHORT).show()
        }
    }

    private fun createMediaController(videoPath: String) {
        videoPlayer.apply {
            setMediaController(MediaController(this@ActivityVideo))
            setVideoURI(Uri.parse(videoPath))
            start()
        }
    }

    companion object {
        const val ARGS_KEY_FILE_PATH = "file_path"
    }
}