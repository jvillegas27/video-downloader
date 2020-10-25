package app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.adapter.VideoFilesAdapter
import app.model.NavigationEvent
import app.viewmodel.MainViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import domain.model.VideoFileModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseMvvmActivity<MainViewModel>() {

    override val viewModelType = MainViewModel::class.java
    private val adapter by lazy {
        VideoFilesAdapter(clickListener = {
            startVideoActivity(it)
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupViews()
        setupInputs()
        setupOutputs()
        viewModel.getDownloadedVideos()
    }

    private fun setupViews() {
        videoList.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = this@MainActivity.adapter
        }

        object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                val removedItem = adapter.removeItem(viewHolder.adapterPosition)
                viewModel.deleteFile(removedItem.filePath)
                Toast.makeText(
                    this@MainActivity,
                    getString(R.string.item_deleted, removedItem.fileName),
                    Toast.LENGTH_SHORT
                ).show()
            }

        }.also {
            ItemTouchHelper(it).attachToRecyclerView(videoList)
        }

    }

    private fun setupOutputs() {
        viewModel.navigationEvent.observe(this, {
            when (it) {
                NavigationEvent.OnPermissionDenied -> displayToast(R.string.permission_storage_denied)
                NavigationEvent.OnLoading -> displayLoading()
                is NavigationEvent.OnDownloadComplete -> processCompleteDownload(it.filePath)
                NavigationEvent.OnFileUrlEmpty -> displayToast(R.string.empty_url)
                NavigationEvent.OnFileAlreadyDownloaded -> displayAlreadyDownloadedDialog()
                NavigationEvent.OnUrlNotValid -> displayToast(R.string.url_is_not_valid)
                is NavigationEvent.OnDownloadedVideos -> setDownloadedItems(it.videoPaths)
                NavigationEvent.OnDownloadConnectionError -> displayToast(R.string.connection_error)
                NavigationEvent.OnDownloadGenericError -> displayToast(R.string.error_general)
            }
        })

        viewModel.progressUpdate.observe(this, {
            loadingProgressBar.isIndeterminate = false
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                loadingProgressBar.setProgress(it, true)
            } else {
                loadingProgressBar.progress = it
            }
            updatePercentageText(it)
        })
    }

    private fun setDownloadedItems(videoPaths: List<VideoFileModel>) {
        adapter.submitList(videoPaths)
    }

    private fun displayAlreadyDownloadedDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(android.R.string.dialog_alert_title)
            .setMessage(R.string.video_file_exist)
            .setPositiveButton(android.R.string.ok, null)
            .create()
            .show()
    }

    private fun updatePercentageText(progress: Int) {
        loadingPercentage.text = progress.toString().plus("%")
    }

    private fun processCompleteDownload(filePath: String) {
        viewModel.getDownloadedVideos()
        displayDownloadButton()
        displayToast(R.string.download_complete)
        startVideoActivity(filePath)
    }

    private fun hideLoadingProgress() {
        loadingProgressBar.apply {
            isVisible = false
            progress = 0
            isVisible = false
        }
        loadingPercentage.isVisible = false
    }

    private fun startVideoActivity(filePath: String) {
        startActivity(
            Intent(this, ActivityVideo::class.java)
                .putExtra(ActivityVideo.ARGS_KEY_FILE_PATH, filePath)
        )
    }

    private fun displayLoading() {
        loadingProgressBar.apply {
            isVisible = true
            isIndeterminate = true
        }
        loadingPercentage.isVisible = true
        updatePercentageText(0)
        btnPause.isVisible = true
        btnCancel.isVisible = true
        btnDownload.visibility = View.INVISIBLE
    }

    private fun displayToast(@StringRes resource: Int) {
        Toast.makeText(this, resource, Toast.LENGTH_SHORT).show()
    }

    private fun setupInputs() {
        btnDownload.setOnClickListener {
            viewModel.downloadVideo(editTextUrl.text.toString())
        }
        btnPause.setOnClickListener {
            viewModel.onPauseClicked()
            btnResume.isVisible = true
            btnPause.isVisible = false
        }
        btnResume.setOnClickListener {
            viewModel.onResumeClicked()
            btnPause.isVisible = true
            btnResume.isVisible = false
        }
        btnCancel.setOnClickListener {
            viewModel.onCancel()
            displayDownloadButton()
        }
    }

    private fun displayDownloadButton() {
        btnPause.isVisible = false
        btnCancel.isVisible = false
        btnDownload.isVisible = true
        hideLoadingProgress()
    }
}